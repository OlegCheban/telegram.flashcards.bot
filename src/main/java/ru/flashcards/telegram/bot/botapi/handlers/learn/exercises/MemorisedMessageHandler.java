package ru.flashcards.telegram.bot.botapi.handlers.learn.exercises;

import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;

public class MemorisedMessageHandler extends CheckExerciseMessageHandler {
    public MemorisedMessageHandler(ExerciseFlashcard currentExercise, DataLayerObject dataLayer) {
        super(currentExercise, dataLayer);
    }

    @Override
    String getCurrentExerciseFlashcardAttributeCheckValue() {
        return "Memorised";
    }
}
