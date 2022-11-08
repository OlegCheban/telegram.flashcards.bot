package ru.flashcards.telegram.bot.botapi.handlers.create;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.InputMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateFlashcardsMessageHandler implements InputMessageHandler {
    private DataLayerObject dataLayer;

    public CreateFlashcardsMessageHandler(DataLayerObject dataLayerObject) {
        this.dataLayer = dataLayerObject;
    }

    @Override
    public List<BotApiMethod<?>> handle(Message message) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(message.getText().split("//")));
        dataLayer.createFlashcard(message.getChatId(), parts.get(0), parts.get(1), parts.get(2), parts.get(3));
        SendMessage replyMessage = new SendMessage();
        replyMessage.setText("Flashcard successfully created");
        replyMessage.setChatId(String.valueOf(message.getChatId()));
        list.add(replyMessage);

        return list;
    }
}
