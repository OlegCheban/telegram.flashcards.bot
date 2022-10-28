package ru.flashcards.telegram.bot.botapi.handlers.learn;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.InputMessageCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class EnableExcerciseMessageHandler implements InputMessageCallbackHandler {
    private CallbackData callbackData;
    private ExerciseDataHandler exerciseDataHandler = new ExerciseDataHandler();

    public EnableExcerciseMessageHandler(CallbackData callbackData) {
        this.callbackData = callbackData;
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();

        exerciseDataHandler.enableExcercise(chatId, callbackData.getEntityCode());

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(toIntExact(messageId));
        editMessage.setText("Done");

        list.add(editMessage);
        return list;
    }
}
