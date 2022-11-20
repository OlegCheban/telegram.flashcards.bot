package ru.flashcards.telegram.bot.botapi;

import java.util.Collections;

public class CallbackFactoryProvider {
    public static CallbackHandlerAbstractFactory getFactory(Message message){
        switch (message){
            case CALLBACK:
                return new CallbackFactory();
        }

        return (CallbackHandlerAbstractFactory<MessageHandler>) (callbackDataJson, dataLayer) -> m -> Collections.emptyList();
    }
}
