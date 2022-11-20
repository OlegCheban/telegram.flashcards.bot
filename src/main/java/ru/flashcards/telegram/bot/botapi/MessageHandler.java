package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface MessageHandler<T extends BotApiObject> {
    List<BotApiMethod<?>> handle(T message);
}
