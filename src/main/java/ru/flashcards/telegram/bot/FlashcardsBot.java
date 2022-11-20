package ru.flashcards.telegram.bot;

import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.flashcards.telegram.bot.botapi.*;
import ru.flashcards.telegram.bot.command.*;
import ru.flashcards.telegram.bot.command.addToLearn.FindFlashcardCommand;
import ru.flashcards.telegram.bot.command.modifyFlashcard.ChangeTranslationCommand;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import java.util.List;

import static ru.flashcards.telegram.bot.botapi.Message.*;

/**
 * @author Oleg Cheban
 */
public class FlashcardsBot extends TelegramLongPollingCommandBot {
    private DataLayerObject dataLayer;

    public FlashcardsBot(DataLayerObject dataLayerObject) {
        dataLayer = dataLayerObject;
        register(new StartCommand("start", "", dataLayer));
        register(new StartLearningCommand("l", "", dataLayer));
        register(new StartWateringSessionCommand("ws", "", dataLayer));
        register(new EnableExcerciseCommand("ee", "", dataLayer));
        register(new DisableExcerciseCommand("de", "", dataLayer));
        register(new SwiperCommand("s", "", dataLayer));
        register(new FindFlashcardCommand("f", "", dataLayer));
        register(new NotificationIntervalSettingsCommand("ni", "", dataLayer));
        register(new TrainingFlashcardsQuantitySettingsCommand("fq", "", dataLayer));
        register(new WateringSessionReplyTimeSettingsCommand("wt", "", dataLayer));
        register(new ChangeTranslationCommand("edit", "", dataLayer));
        register(new HelpCommand("h", ""));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()){
            execute(handleMessageInput(update.getMessage()));
        } else if (update.hasCallbackQuery()) {
            execute(handleCallbackQueryInput(update.getCallbackQuery()));
        }
    }

    private List<BotApiMethod<?>> handleMessageInput(Message message){
        MessageHandler<Message> handler = null;
        MessageHandlerAbstractFactory factory = null;

        if (dataLayer.isLearnFlashcardState(message.getChatId())){
            //learning mode
            factory = MessageFactoryProvider.getFactory(EXERCISE);

        } else if (dataLayer.isWateringSession(message.getChatId())){
            //watering session
            factory = MessageFactoryProvider.getFactory(WATERING_SESSION);

        } else {
            //other messages
            factory = MessageFactoryProvider.getFactory(FLASHCARD);

        }

        assert factory != null;
        handler = (MessageHandler<Message>) factory.getHandler(message, dataLayer);
        assert handler != null;

        return handler.handle(message);
    }

    private List<BotApiMethod<?>> handleCallbackQueryInput(CallbackQuery callbackQuery){
        CallbackFactory factory = (CallbackFactory) CallbackFactoryProvider.getFactory(CALLBACK);
        assert factory != null;
        MessageHandler<CallbackQuery> handler = factory.getHandler(callbackQuery.getData(), dataLayer);
        assert handler != null;

        return handler.handle(callbackQuery);
    }

    private void execute(List<BotApiMethod<?>> list){
        list.forEach(messageAnswer -> {
            try {
                execute(messageAnswer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public String getBotUsername() {
        return System.getenv().get("FLASHCARDS_BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv().get("FLASHCARDS_BOT_TOKEN");
    }
}


