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

/**
 * @author Oleg Cheban
 */
public class FlashcardsBot extends TelegramLongPollingCommandBot {
    private MessageHandlerFactory messageHandlerFactory = new MessageHandlerFactory();
    private ExerciseMessageHandlerFactory exerciseMessageHandlerFactory = new ExerciseMessageHandlerFactory();
    private CallbackHandlerFactory callbackHandlerFactory = new CallbackHandlerFactory();
    private DataLayerObject dataLayer;

    public FlashcardsBot(DataLayerObject dataLayerObject) {
        dataLayer = dataLayerObject;
        register(new StartCommand("start", "", dataLayer));
        register(new StartLearningCommand("l", "", dataLayer));
        register(new EnableExcerciseCommand("exe", "", dataLayer));
        register(new DisableExcerciseCommand("exd", "", dataLayer));
        register(new SwiperCommand("s", "", dataLayer));
        register(new FindFlashcardCommand("f", "", dataLayer));
        register(new NotificationIntervalSettingsCommand("i", "", dataLayer));
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
        if (dataLayer.isLearnFlashcardState(message.getChatId())){
            //learning mode
            InputMessageHandler exerciseMessageHandler = exerciseMessageHandlerFactory.getHandler(message, dataLayer);
            List<BotApiMethod<?>> exerciseHandleMessageQuery = exerciseMessageHandler.handle(message);
            return exerciseHandleMessageQuery;
        } else {
            //other messages
            InputMessageHandler inputMessageHandler = messageHandlerFactory.getHandler(message.getText(), dataLayer);
            List<BotApiMethod<?>> handleMessageQuery = inputMessageHandler.handle(message);
            return handleMessageQuery;
        }
    }

    private List<BotApiMethod<?>> handleCallbackQueryInput(CallbackQuery callbackQuery){
        InputMessageCallbackHandler inputMessageCallbackHandler = callbackHandlerFactory.getHandler(callbackQuery.getData(), dataLayer);
        List<BotApiMethod<?>> handleCallbackQuery = inputMessageCallbackHandler.handle(callbackQuery);
        return handleCallbackQuery;
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


