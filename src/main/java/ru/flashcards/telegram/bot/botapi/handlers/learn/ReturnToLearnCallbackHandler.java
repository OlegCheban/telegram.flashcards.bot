package ru.flashcards.telegram.bot.botapi.handlers.learn;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.InputMessageCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.FlashcardDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.SpacedRepetitionNotificationDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class ReturnToLearnCallbackHandler implements InputMessageCallbackHandler {
    private CallbackData callbackData;
    private FlashcardDataHandler flashcardDataHandler = new FlashcardDataHandler();
    private SpacedRepetitionNotificationDataHandler spacedRepetitionNotificationDataHandler = new SpacedRepetitionNotificationDataHandler();
    private ExerciseDataHandler exerciseDataHandler = new ExerciseDataHandler();

    public ReturnToLearnCallbackHandler(CallbackData callbackData) {
        this.callbackData = callbackData;
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();
        Long userFlashcardId = callbackData.getEntityId();

        UserFlashcard flashcard = flashcardDataHandler.findUserFlashcardById(userFlashcardId);
        spacedRepetitionNotificationDataHandler.deleteSpacedRepetitionHistory(userFlashcardId);
        exerciseDataHandler.deleteExerciseStat(userFlashcardId);
        exerciseDataHandler.returnToLearn(userFlashcardId);

        EditMessageText translationMessage = new EditMessageText();
        translationMessage.setChatId(String.valueOf(chatId));
        translationMessage.setMessageId(toIntExact(messageId));
        translationMessage.enableMarkdown(true);
        translationMessage.setText("*Flashcard returned to learn*\n*"+flashcard.getWord()+"* \\[" + flashcard.getTranscription() + "]\n\n"+flashcard.getDescription() + "\n\n"+flashcard.getTranslation());

        list.add(translationMessage);
        return list;
    }
}