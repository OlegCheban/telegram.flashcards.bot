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
    private MessageFactory messageFactory;


    public MessageHandlerAbstractFactory getFactory(MessageFactoryType messageFactoryType){
        switch (messageFactoryType){
            case EXERCISE:
                return exerciseMessageFactory;
            case WATERING_SESSION:
                return wateringSessionMessageFactory;
            case OTHER_MESSAGES:
                return messageFactory;
        }

        return (MessageHandlerAbstractFactory<MessageHandler>) (msg) -> m -> Collections.emptyList();
    }
}
