package ru.flashcards.telegram.bot.botapi.handlers.wateringSession;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.botapi.wateringSession.WateringSession;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;
import ru.flashcards.telegram.bot.utils.RandomMessageText;
import ru.flashcards.telegram.bot.utils.WateringSessionTimingSingleton;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CheckWateringSessionExerciseMessageHandler implements MessageHandler<Message> {
    private UserFlashcard userFlashcard;
    private DataLayerObject dataLayer;
    private List<BotApiMethod<?>> list = new ArrayList<>();

    public CheckWateringSessionExerciseMessageHandler(UserFlashcard userFlashcard, DataLayerObject dataLayerObject){
        this.userFlashcard = userFlashcard;
        this.dataLayer = dataLayerObject;
    }

    @Override
    public List<BotApiMethod<?>> handle(Message message){
        Boolean checkExerciseResult = checkExercise(message.getText().trim());
        Boolean checkTimingResult = checkTiming(message.getChatId());
        sendResultMessage(checkExerciseResult, checkTimingResult, message.getChatId());
        dataLayer.setWateringSessionDate(userFlashcard.getId());
        WateringSession wateringSession = new WateringSession(dataLayer);
        list.add(wateringSession.newFlashcard(message.getChatId()));

        return list;
    }

    private boolean checkExercise(String checkValue){
        return checkValue.equalsIgnoreCase(userFlashcard.getTranslation().trim());
    }

    private Boolean checkTiming(Long chatId){
        LocalDateTime startExerciseDateTime = WateringSessionTimingSingleton.getStartDateTime(chatId);
        long diff = ChronoUnit.SECONDS.between(startExerciseDateTime, LocalDateTime.now());
        return diff <= dataLayer.getWateringSessionReplyTime(chatId);
    }

    private void sendResultMessage(Boolean checkExerciseResult, Boolean checkTimingResult, Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (!checkTimingResult){
            sendMessage.setText(RandomMessageText.getNegativeMessage() + ". It's time.");
        } else {
            sendMessage.setText(checkExerciseResult ?
                    RandomMessageText.getPositiveMessage() :
                    RandomMessageText.getNegativeMessage()
            );
        }

        list.add(sendMessage);
    }
}
