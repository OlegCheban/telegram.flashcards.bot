package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

public interface InputMessageCallbackHandler {
    List<BotApiMethod<?>> handle(CallbackQuery callbackQuery);
}
