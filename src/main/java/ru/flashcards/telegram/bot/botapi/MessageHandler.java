package ru.flashcards.telegram.bot.botapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface MessageHandler<T extends BotApiObject> {
    List<BotApiMethod<?>> handle(T message);

    default CallbackData getCallbackData(String callbackDataJson){
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callback = null;
        try {
            callback = objectMapper.readValue(callbackDataJson, CallbackData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return callback;
    }
}
