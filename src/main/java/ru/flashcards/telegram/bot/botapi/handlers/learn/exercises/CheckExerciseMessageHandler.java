package ru.flashcards.telegram.bot.botapi.handlers.learn.exercises;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.flashcards.telegram.bot.botapi.InputMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.utils.RandomMessageText;

import java.util.ArrayList;
import java.util.List;

abstract class CheckExerciseMessageHandler implements InputMessageHandler {
    private ExerciseFlashcard currentExercise;
    private DataLayerObject dataLayer;
    private List<BotApiMethod<?>> list = new ArrayList<>();
    private Long chatId;

    public CheckExerciseMessageHandler(ExerciseFlashcard currentFlashcardExercise, DataLayerObject dataLayerObject){
        currentExercise = currentFlashcardExercise;
        dataLayer = dataLayerObject;
    }

    abstract String getCurrentExerciseFlashcardAttributeCheckValue();

    @Override
    public List<BotApiMethod<?>> handle(Message message){
        chatId = message.getChatId();
        checkExercise(message.getText().trim());

        if (dataLayer.getCurrentExercise(chatId) != null){
            dataLayer.setLock(chatId, false);
        } else {
           stopLearning();
        }

        return list;
    }

    private void checkExercise(String checkValue){
        Boolean isCorrentAnswer = checkValue.equalsIgnoreCase(getCurrentExerciseFlashcardAttributeCheckValue().trim());

        dataLayer.insertExerciseResult(
                currentExercise.getUserFlashcardId(),
                currentExercise.getExerciseCode(),
                isCorrentAnswer
        );

        SendMessage sendMessage = sendAnswer(isCorrentAnswer);
        sendMessage.setChatId(String.valueOf(chatId));
        list.add(sendMessage);
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
        //disable learning mode
        dataLayer.setLearnFlashcardState(chatId, false);
        //remove keyboard
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardRemove);

        list.add(sendMessage);
    }

    private SendMessage sendAnswer(Boolean result){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(result ?
                RandomMessageText.getPositiveMessage() :
                RandomMessageText.getNegativeMessage()
        );

        return  sendMessage;
    }

    protected ExerciseFlashcard getCurrentExercise(){
        return currentExercise;
    }
}
