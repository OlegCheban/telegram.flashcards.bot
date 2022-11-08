package ru.flashcards.telegram.bot.botapi.handlers.learn.exercises;

import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;

public class CompleteTheGapsMessageHandler extends CheckExerciseMessageHandler {
    public CompleteTheGapsMessageHandler(ExerciseFlashcard currentExercise, DataLayerObject dataLayerObject) {
        super(currentExercise, dataLayerObject);
    }

    @Override
    String getCurrentExerciseFlashcardAttributeCheckValue() {
        return getCurrentExercise().getWord();
    }
}
