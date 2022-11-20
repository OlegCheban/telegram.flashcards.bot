package ru.flashcards.telegram.bot.botapi.handlers.examples;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import java.util.ArrayList;
import java.util.List;

public class FlashcardUsageExamplesCallbackHandler implements MessageHandler<CallbackQuery> {
    private CallbackData callbackData;
    private DataLayerObject dataLayer;

    public FlashcardUsageExamplesCallbackHandler(CallbackData callbackData, DataLayerObject dataLayerObject) {
        this.callbackData = callbackData;
        this.dataLayer = dataLayerObject;
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        Long userFlashcardId = callbackData.getEntityId();

        dataLayer.getExamplesByUserFlashcardId(userFlashcardId).forEach(example -> {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setText(example);
            sendMessage.enableMarkdown(true);

            list.add(sendMessage);
        });

        return list;
    }
}
