package ru.flashcards.telegram.bot.botapi;

import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;

public interface CallbackHandlerAbstractFactory<T> {
    T getHandler(CallbackData callbackData);
}
