package ru.flashcards.telegram.bot.botapi.handlers.learn;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.flashcards.telegram.bot.botapi.InputMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;

import java.util.ArrayList;
import java.util.List;

public class StopLearningMessageHandler implements InputMessageHandler {
    private ExerciseDataHandler exerciseDataHandler;

    public StopLearningMessageHandler(ExerciseDataHandler exerciseDataHandler) {
        this.exerciseDataHandler = exerciseDataHandler;
    }

    @Override
    public List<BotApiMethod<?>> handle(Message message) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);

        StringBuffer msg = new StringBuffer ();
        List<String> learned = exerciseDataHandler.getLearnedFlashcards(message.getChatId());
        if (!learned.isEmpty()){
            msg.append("Well done! Learned flashcards:\n");
            learned.forEach(v -> {
                msg.append(v);
                msg.append("\n");
            });
            msg.append("\n");
        }

        msg.append("Keep learning!");

        //фиксация выученных карточек
        exerciseDataHandler.refreshLearnedFlashcards();
        //отключить режим обучения
        exerciseDataHandler.setLearnFlashcardState(message.getChatId(), false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(msg.toString());
        sendMessage.setReplyMarkup(replyKeyboardRemove);

        list.add(sendMessage);

        return list;
    }
}
