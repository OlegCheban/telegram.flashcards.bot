package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

public interface MessageHandlerAbstractFactory<T> {
    T getHandler(Message message, DataLayerObject dataLayer);
}
