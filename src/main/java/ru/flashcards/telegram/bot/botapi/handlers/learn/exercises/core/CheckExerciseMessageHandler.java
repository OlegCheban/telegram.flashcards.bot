package ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.core;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.utils.RandomMessageText;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class CheckExerciseMessageHandler implements MessageHandler<Message> {
    @Inject
    private DataLayerObject dataLayer;
    @Inject
    private ExerciseProvider exerciseProvider;

    private ExerciseFlashcard currentExercise;
    private List<BotApiMethod<?>> list = new ArrayList<>();
    private Long chatId;

    protected abstract String getCurrentExerciseFlashcardAttributeCheckValue();

    @Override
    public List<BotApiMethod<?>> handle(Message message){
        currentExercise = dataLayer.getCurrentExercise(message.getChatId());
        chatId = message.getChatId();
        Boolean result = checkExercise(message.getText().trim());
        sendResultMessage(result, chatId);

        if (dataLayer.getCurrentExercise(chatId) != null){
            list.add(exerciseProvider.newExercise(chatId));
        } else {
           stopLearning();
        }

        return list;
    }

    private boolean checkExercise(String checkValue){
        Boolean isCorrentAnswer =
                checkValue.equalsIgnoreCase(
                    getCurrentExerciseFlashcardAttributeCheckValue().trim()
                );

        dataLayer.insertExerciseResult(
                currentExercise.getUserFlashcardId(),
                currentExercise.getExerciseCode(),
                isCorrentAnswer
        );

        return isCorrentAnswer;
    }

    private void stopLearning(){
        StringBuffer msg = new StringBuffer ();
        msg.append("Well done! You have just learned flashcards:\n");
        dataLayer.getCurrentBatchFlashcardsByUser(chatId).forEach(v -> {
            msg.append(v);
            msg.append("\n");
        });
        msg.append("\n");
        msg.append("Keep learning!");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(msg.toString());
        sendMessage.setChatId(String.valueOf(chatId));

        //update learned flashcards
        dataLayer.refreshLearnedFlashcards();
        //disable learn mode
        dataLayer.setLearnFlashcardState(chatId, false);

        //remove keyboard
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardRemove);

        list.add(sendMessage);
    }

    private void sendResultMessage(Boolean result, Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(result ?
                RandomMessageText.getPositiveMessage() :
                RandomMessageText.getNegativeMessage()
        );

        list.add(sendMessage);
    }

    public ExerciseFlashcard getCurrentExercise() {
        return currentExercise;
    }
}
