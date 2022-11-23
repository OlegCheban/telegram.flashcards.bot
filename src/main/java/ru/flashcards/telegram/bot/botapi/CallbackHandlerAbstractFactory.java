package ru.flashcards.telegram.bot.botapi;

import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

public interface CallbackHandlerAbstractFactory<T> {
    T getHandler(String callbackDataJson);
}
