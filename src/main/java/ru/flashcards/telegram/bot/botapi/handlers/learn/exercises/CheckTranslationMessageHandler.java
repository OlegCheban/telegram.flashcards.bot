package ru.flashcards.telegram.bot.botapi.handlers.learn.exercises;

import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;

public class CheckTranslationMessageHandler extends CheckExerciseMessageHandler {
    public CheckTranslationMessageHandler(ExerciseFlashcard currentExercise, ExerciseDataHandler exerciseDataHandler) {
        super(currentExercise, exerciseDataHandler);
    }

    @Override
    String getCurrentExerciseFlashcardAttributeCheckValue() {
        return getCurrentExercise().getTranslation();
    }
}
