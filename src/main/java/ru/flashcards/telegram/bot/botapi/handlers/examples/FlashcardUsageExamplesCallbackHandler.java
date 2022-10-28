package ru.flashcards.telegram.bot.botapi.handlers.examples;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.InputMessageCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.FlashcardDataHandler;

import java.util.ArrayList;
import java.util.List;

public class FlashcardUsageExamplesCallbackHandler implements InputMessageCallbackHandler {
    private CallbackData callbackData;
    private FlashcardDataHandler flashcardDataHandler = new FlashcardDataHandler();

    public FlashcardUsageExamplesCallbackHandler(CallbackData callbackData) {
        this.callbackData = callbackData;
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        Long userFlashcardId = callbackData.getEntityId();

        flashcardDataHandler.getExamplesByUserFlashcardId(userFlashcardId).forEach(example -> {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setText(example);
            sendMessage.enableMarkdown(true);

            list.add(sendMessage);
        });

        return list;
    }
}
