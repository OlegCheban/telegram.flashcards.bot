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
import ru.flashcards.telegram.bot.command.ChangeTranslationCommand;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import java.util.List;

import static ru.flashcards.telegram.bot.botapi.Literals.*;
import static ru.flashcards.telegram.bot.botapi.MessageFactoryType.*;

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

        register(new StartCommand(START_COMMAND, "", dataLayer));
        register(new StartLearningCommand(START_LEARNING_COMMAND, "", dataLayer));
        register(new StartWateringSessionCommand(START_WATERING_SESSION_COMMAND, "", dataLayer));
        register(new EnableExcerciseCommand(ENABLE_EXCERCISE_COMMAND, "", dataLayer));
        register(new DisableExcerciseCommand(DISABLE_EXCERCISE_COMMAND, "", dataLayer));
        register(new SwiperCommand(OPEN_SWIPER_COMMAND, "", dataLayer));
        register(new FindFlashcardCommand(FIND_FLASHCARD_COMMAND, "", dataLayer));
        register(new NotificationIntervalSettingsCommand(NOTIFICATION_INTERVAL_SETTINGS_COMMAND, "", dataLayer));
        register(new TrainingFlashcardsQuantitySettingsCommand(TRAINING_FLASHCARDS_QUANTITY_SETTINGS_COMMAND, "", dataLayer));
        register(new WateringSessionReplyTimeSettingsCommand(WATERING_SESSION_REPLY_TIME_SETTINGS_COMMAND, "", dataLayer));
        register(new ChangeTranslationCommand(CHANGE_TRANSLATION_COMMAND, "", dataLayer));
        register(new HelpCommand(HELP_COMMAND, ""));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        RequestContext requestContext = container.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
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
            factory = messageFactoryProvider.getFactory(OTHER_MESSAGES);

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


