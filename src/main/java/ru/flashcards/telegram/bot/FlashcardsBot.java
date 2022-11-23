package ru.flashcards.telegram.bot;

import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.UnboundLiteral;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
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
    private Weld weld = new Weld();
    private WeldContainer container = weld.initialize();
    private DataLayerObject dataLayer;
    private MessageFactoryProvider messageFactoryProvider;
    private CallbackFactoryProvider callbackFactoryProvider;

    public FlashcardsBot() {
        dataLayer = container.select(DataLayerObject.class).get();
        messageFactoryProvider = container.select(MessageFactoryProvider.class).get();
        callbackFactoryProvider = container.select(CallbackFactoryProvider.class).get();

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
        RequestContext requestContext= container.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
        requestContext.activate();

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
            factory = messageFactoryProvider.getFactory(EXERCISE);

        } else if (dataLayer.isWateringSession(message.getChatId())){
            //watering session
            factory = messageFactoryProvider.getFactory(WATERING_SESSION);

        } else {
            //other messages
            factory = messageFactoryProvider.getFactory(FLASHCARD);

        }

        assert factory != null;
        handler = (MessageHandler<Message>) factory.getHandler(message);
        assert handler != null;

        return handler.handle(message);
    }

    private List<BotApiMethod<?>> handleCallbackQueryInput(CallbackQuery callbackQuery){
        CallbackFactory factory = (CallbackFactory) callbackFactoryProvider.getFactory(CALLBACK);
        assert factory != null;
        MessageHandler<CallbackQuery> handler = factory.getHandler(callbackQuery.getData());
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


