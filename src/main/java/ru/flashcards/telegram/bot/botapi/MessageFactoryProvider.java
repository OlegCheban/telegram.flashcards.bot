package ru.flashcards.telegram.bot.botapi;

import java.util.Collections;

public class MessageFactoryProvider {
    public static MessageHandlerAbstractFactory getFactory(Message message){
        switch (message){
            case EXERCISE:
                return new ExerciseMessageFactory();
            case WATERING_SESSION:
                return new WateringSessionMessageFactory();
            case FLASHCARD:
                return new CreateFlashcardMessageFactory();
        }

        return (MessageHandlerAbstractFactory<MessageHandler>) (message1, dataLayer) -> m -> Collections.emptyList();
    }
}
