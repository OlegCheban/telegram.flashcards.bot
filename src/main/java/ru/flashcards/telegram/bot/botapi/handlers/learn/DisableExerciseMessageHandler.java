package ru.flashcards.telegram.bot.botapi.handlers.learn;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class DisableExerciseMessageHandler implements MessageHandler<CallbackQuery> {
    @Inject
    private DataLayerObject dataLayer;

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        CallbackData callbackData = jsonToCallbackData(callbackQuery.getData());
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();

        dataLayer.disableExcercise(chatId, callbackData.getEntityCode());

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(toIntExact(messageId));
        editMessage.setText("Exercise is disabled successfully.");

        list.add(editMessage);
        return list;
    }
}
