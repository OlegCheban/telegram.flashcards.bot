package ru.flashcards.telegram.bot.botapi;

public interface CallbackHandlerAbstractFactory<T> {
    T getHandler(String callbackDataJson);
}
