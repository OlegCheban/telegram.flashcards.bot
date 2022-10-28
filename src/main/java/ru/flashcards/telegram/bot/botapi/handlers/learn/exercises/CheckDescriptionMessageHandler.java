package ru.flashcards.telegram.bot.botapi.handlers.learn.exercises;

import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;

public class CheckDescriptionMessageHandler extends CheckExerciseMessageHandler {
    public CheckDescriptionMessageHandler(ExerciseFlashcard currentExercise, ExerciseDataHandler exerciseDataHandler) {
        super(currentExercise, exerciseDataHandler);
    }

    @Override
    String getCurrentExerciseFlashcardAttributeCheckValue() {
        return getCurrentExercise().getDescription();
    }
}
