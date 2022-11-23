package ru.flashcards.telegram.bot.botapi.handlers.create;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateDefinitionMessageHandler implements MessageHandler<Message> {
    @Inject
    private DataLayerObject dataLayer;

    @Override
    public List<BotApiMethod<?>> handle(Message message) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(message.getText().split("##")));
        dataLayer.createDefinition(message.getChatId(), parts.get(0), parts.get(1));

        SendMessage replyMessage = new SendMessage();
        replyMessage.setText("Definition successfully created");
        replyMessage.setChatId(String.valueOf(message.getChatId()));
        list.add(replyMessage);

        return list;
    }
}
