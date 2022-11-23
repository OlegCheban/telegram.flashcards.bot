package ru.flashcards.telegram.bot.botapi.handlers.learn.exercises;

import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.core.CheckExerciseMessageHandler;

public class CheckDescriptionMessageHandler extends CheckExerciseMessageHandler {
    @Override
    protected String getCurrentExerciseFlashcardAttributeCheckValue() {
        return getCurrentExercise().getDescription();
    }
}
