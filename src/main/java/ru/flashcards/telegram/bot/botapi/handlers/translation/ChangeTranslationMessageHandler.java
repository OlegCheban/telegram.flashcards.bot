package ru.flashcards.telegram.bot.botapi.handlers.translation;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.botapi.UserFlashcardModificationBuffer;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ChangeTranslationMessageHandler implements MessageHandler<Message> {
    @Inject
    private DataLayerObject dataLayer;

    @Override
    public List<BotApiMethod<?>> handle(Message message) {
        List<BotApiMethod<?>> list = new ArrayList<>();

        dataLayer.editTranslation(
                UserFlashcardModificationBuffer.getUserFlashcardId(message.getChatId()),
                message.getText()
        );
        UserFlashcardModificationBuffer.removeRequest(message.getChatId());

        SendMessage replyMessage = new SendMessage();
        replyMessage.setText("Translation is changed successfully");
        replyMessage.setChatId(String.valueOf(message.getChatId()));
        list.add(replyMessage);

        return list;
    }
}
