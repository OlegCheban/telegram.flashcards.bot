package ru.flashcards.telegram.bot.botapi.handlers.learn.exercises;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import java.util.ArrayList;
import java.util.List;

public class StopLearningMessageHandler implements MessageHandler<Message> {
    private DataLayerObject dataLayer;

    public StopLearningMessageHandler(DataLayerObject dataLayerObject) {
        this.dataLayer = dataLayerObject;
    }

    @Override
    public List<BotApiMethod<?>> handle(Message message) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);

        StringBuffer msg = new StringBuffer ();
        List<String> learned = dataLayer.getLearnedFlashcards(message.getChatId());
        if (!learned.isEmpty()){
            msg.append("Well done! You have just learned flashcards:\n");
            learned.forEach(v -> {
                msg.append(v);
                msg.append("\n");
            });
            msg.append("\n");
        }

        msg.append("Keep learning!");

        //фиксация выученных карточек
        dataLayer.refreshLearnedFlashcards();
        //отключить режим обучения
        dataLayer.setLearnFlashcardState(message.getChatId(), false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(msg.toString());
        sendMessage.setReplyMarkup(replyKeyboardRemove);

        list.add(sendMessage);

        return list;
    }
}
