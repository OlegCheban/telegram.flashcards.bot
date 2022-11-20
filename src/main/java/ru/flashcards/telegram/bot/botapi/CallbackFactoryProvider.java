package ru.flashcards.telegram.bot.botapi;

public class CallbackFactoryProvider {
    public static CallbackHandlerAbstractFactory getFactory(Message message){
        switch (message){
            case CALLBACK:
                return new CallbackFactory();
        }
        return null;
    }
}
