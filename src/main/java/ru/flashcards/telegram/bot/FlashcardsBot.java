package ru.flashcards.telegram.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;
import ru.flashcards.telegram.bot.command.*;
import ru.flashcards.telegram.bot.command.addToLearn.FindFlashcardCommand;
import ru.flashcards.telegram.bot.command.ChangeTranslationCommand;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.exception.JsonProcessingRuntimeException;
import ru.flashcards.telegram.bot.exception.TelegramApiRuntimeException;

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

    private List<BotApiMethod<?>> handleMessageInput(Message message) {
        MessageHandlerAbstractFactory factory = getFactory(message);
        MessageHandler<Message> handler = (MessageHandler<Message>) factory.getHandler(message);
        return handler.handle(message);
    }

    private MessageHandlerAbstractFactory getFactory(Message message) {
        long chatId = message.getChatId();
        if (dataLayer.isLearnFlashcardState(chatId)) {
            return messageFactoryProvider.getFactory(EXERCISE);
        } else if (dataLayer.isWateringSession(chatId)) {
            return messageFactoryProvider.getFactory(WATERING_SESSION);
        } else {
            return messageFactoryProvider.getFactory(OTHER_MESSAGES);
        }
    }

    private List<BotApiMethod<?>> handleCallbackQueryInput(CallbackQuery callbackQuery) {
        CallbackData callbackData = getCallbackData(callbackQuery);
        MessageHandler<CallbackQuery> handler = getCallbackHandler(callbackData);
        return handler.handle(callbackQuery);
    }

    private CallbackData getCallbackData(CallbackQuery callbackQuery) {
        try {
            return new ObjectMapper().readValue(callbackQuery.getData(), CallbackData.class);
        } catch (JsonProcessingException e) {
            throw new JsonProcessingRuntimeException(e);
        }
    }

    private MessageHandler<CallbackQuery> getCallbackHandler(CallbackData callbackData) {
        CallbackFactory factory = (CallbackFactory) callbackFactoryProvider.getFactory(CALLBACK);
        return factory.getHandler(callbackData);
    }

    private void execute(List<BotApiMethod<?>> list){
        list.forEach(messageAnswer -> {
            try {
                execute(messageAnswer);
            } catch (TelegramApiException e) {
                throw new TelegramApiRuntimeException(e);
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


