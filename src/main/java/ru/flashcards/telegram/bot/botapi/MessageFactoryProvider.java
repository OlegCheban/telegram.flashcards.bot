package ru.flashcards.telegram.bot.botapi;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Collections;

@RequestScoped
public class MessageFactoryProvider {
    @Inject
    private ExerciseMessageFactory exerciseMessageFactory;
    @Inject
    private WateringSessionMessageFactory wateringSessionMessageFactory;
    @Inject
    private CreateFlashcardMessageFactory createFlashcardMessageFactory;


    public MessageHandlerAbstractFactory getFactory(Message message){
        switch (message){
            case EXERCISE:
                return exerciseMessageFactory;
            case WATERING_SESSION:
                return wateringSessionMessageFactory;
            case FLASHCARD:
                return createFlashcardMessageFactory;
        }

        return (MessageHandlerAbstractFactory<MessageHandler>) (msg) -> m -> Collections.emptyList();
    }
}
