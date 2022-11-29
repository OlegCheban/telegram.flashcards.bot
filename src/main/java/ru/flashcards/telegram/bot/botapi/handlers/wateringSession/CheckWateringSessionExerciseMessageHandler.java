package ru.flashcards.telegram.bot.botapi.handlers.wateringSession;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.botapi.wateringSession.WateringSession;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;
import ru.flashcards.telegram.bot.utils.RandomMessageText;
import ru.flashcards.telegram.bot.botapi.WateringSessionTimingSingleton;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CheckWateringSessionExerciseMessageHandler implements MessageHandler<Message> {
    @Inject
    private DataLayerObject dataLayer;
    @Inject
    private WateringSession wateringSession;
    private UserFlashcard userFlashcard;
    private List<BotApiMethod<?>> list = new ArrayList<>();
    private Long chatId;

    @Override
    public List<BotApiMethod<?>> handle(Message message){
        chatId = message.getChatId();
        userFlashcard = dataLayer.getUserFlashcardForWateringSession(chatId);

        createResultMessage(
                checkExercise(message.getText().trim()),
                checkTiming(chatId)
        );
        next();

        return list;
    }

    private void createResultMessage(Boolean checkExerciseResult, Boolean checkTimingResult){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (!checkTimingResult){
            sendMessage.setText("Time has run out.");
        } else {
            sendMessage.setText(checkExerciseResult ?
                    RandomMessageText.getPositiveMessage() :
                    RandomMessageText.getNegativeMessage()
            );
        }

        list.add(sendMessage);
    }

    private boolean checkExercise(String checkValue){
        return checkValue.equalsIgnoreCase(userFlashcard.getTranslation().trim());
    }

    private Boolean checkTiming(Long chatId){
        LocalDateTime startExerciseDateTime = WateringSessionTimingSingleton.getStartDateTime(chatId);
        long diff = ChronoUnit.SECONDS.between(startExerciseDateTime, LocalDateTime.now());
        return diff <= dataLayer.getWateringSessionReplyTime(chatId);
    }

    private void next(){
        dataLayer.finishedLastFlashcard(userFlashcard.getId());
        list.add(wateringSession.newFlashcard(chatId));
    }
}
