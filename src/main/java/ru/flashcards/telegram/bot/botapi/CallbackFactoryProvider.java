package ru.flashcards.telegram.bot.botapi;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Collections;

@RequestScoped
public class CallbackFactoryProvider {
    @Inject
    private CallbackFactory callbackFactory;

    public CallbackHandlerAbstractFactory getFactory(Message message){
        switch (message){
            case CALLBACK:
                return callbackFactory;
        }

        return (CallbackHandlerAbstractFactory<MessageHandler>) (callbackDataJson) -> m -> Collections.emptyList();
    }
}
