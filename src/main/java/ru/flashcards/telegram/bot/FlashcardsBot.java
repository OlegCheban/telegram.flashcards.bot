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
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;

import java.util.List;

/**
 * @author Oleg Cheban
 */
public class FlashcardsBot extends TelegramLongPollingCommandBot {
    private MessageHandlerFactory messageHandlerFactory = new MessageHandlerFactory();
    private ExerciseMessageHandlerFactory exerciseMessageHandlerFactory = new ExerciseMessageHandlerFactory();
    private CallbackHandlerFactory callbackHandlerFactory = new CallbackHandlerFactory();
    private ExerciseDataHandler exerciseDataHandler = new ExerciseDataHandler();

    public FlashcardsBot() {
        register(new StartCommand("start", ""));
        register(new StartLearningCommand("l", "", exerciseDataHandler));
        register(new EnableExcerciseCommand("exe", "", exerciseDataHandler));
        register(new DisableExcerciseCommand("exd", "", exerciseDataHandler));
        register(new SwiperCommand("s", ""));
        register(new FindFlashcardCommand("f", ""));
        register(new NotificationIntervalSettingsCommand("i", ""));
        register(new ChangeTranslationCommand("edit", ""));
        register(new HelpCommand("h", ""));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()){
            processMessageInput(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            processCallbackQueryInput(update.getCallbackQuery());
        }
    }

    private void processMessageInput(Message message){
        if (exerciseDataHandler.isLearnFlashcardState(message.getChatId())){
            //learning mode
            InputMessageHandler exerciseMessageHandler = exerciseMessageHandlerFactory.getHandler(message, exerciseDataHandler);
            List<BotApiMethod<?>> exerciseHandleMessageQuery = exerciseMessageHandler.handle(message);
            execute(exerciseHandleMessageQuery);
        } else {
            //other messages
            InputMessageHandler inputMessageHandler = messageHandlerFactory.getHandler(message.getText());
            List<BotApiMethod<?>> handleMessageQuery = inputMessageHandler.handle(message);
            execute(handleMessageQuery);
        }
    }

    private void processCallbackQueryInput(CallbackQuery callbackQuery){
        InputMessageCallbackHandler inputMessageCallbackHandler = callbackHandlerFactory.getHandler(callbackQuery.getData());
        List<BotApiMethod<?>> handleCallbackQuery = inputMessageCallbackHandler.handle(callbackQuery);
        execute(handleCallbackQuery);
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


