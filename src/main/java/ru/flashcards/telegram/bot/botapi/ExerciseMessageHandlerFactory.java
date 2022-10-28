package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.learn.StopLearningMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.CheckDescriptionMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.CheckSpellingMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.CheckTranslationMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.CompleteTheGapsMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import java.util.Collections;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class ExerciseMessageHandlerFactory {
    public InputMessageHandler getHandler(Message message, ExerciseDataHandler exerciseDataHandler){
        if (message.getText().equals(STOP_LEARNING)){
            return new StopLearningMessageHandler(exerciseDataHandler);
        } else {
            ExerciseFlashcard currentExercise = exerciseDataHandler.getCurrentExercise(message.getChatId());

            switch (currentExercise.getExerciseCode()){
                case CHECK_DESCRIPTION:
                    return new CheckDescriptionMessageHandler(currentExercise, exerciseDataHandler);
                case CHECK_TRANSLATION:
                    return new CheckTranslationMessageHandler(currentExercise, exerciseDataHandler);
                case CHECK_SPELLING:
                case CHECK_SPELLING_WITH_HELPS:
                    return new CheckSpellingMessageHandler(currentExercise, exerciseDataHandler);
                case COMPLETE_THE_GAPS:
                    return new CompleteTheGapsMessageHandler(currentExercise, exerciseDataHandler);
            }
        }

        return m -> Collections.emptyList();
    }
}
