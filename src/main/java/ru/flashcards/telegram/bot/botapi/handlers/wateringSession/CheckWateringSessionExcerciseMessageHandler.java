package ru.flashcards.telegram.bot.botapi.handlers.wateringSession;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.InputMessageHandler;
import ru.flashcards.telegram.bot.botapi.wateringSession.WateringSession;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;
import ru.flashcards.telegram.bot.utils.RandomMessageText;

import java.util.ArrayList;
import java.util.List;

public class CheckWateringSessionExcerciseMessageHandler implements InputMessageHandler {
    private UserFlashcard userFlashcard;
    private DataLayerObject dataLayer;
    private List<BotApiMethod<?>> list = new ArrayList<>();

    public CheckWateringSessionExcerciseMessageHandler(UserFlashcard userFlashcard, DataLayerObject dataLayerObject){
        this.userFlashcard = userFlashcard;
        this.dataLayer = dataLayerObject;
    }

    @Override
    public List<BotApiMethod<?>> handle(Message message){
        Boolean result = checkExercise(message.getText().trim());
        sendResultMessage(result, message.getChatId());

        dataLayer.setWateringSessionDate(userFlashcard.getId());

        WateringSession wateringSession = new WateringSession(dataLayer);
        list.add(wateringSession.newFlashcard(message.getChatId()));

        return list;
    }

    private boolean checkExercise(String checkValue){
        return checkValue.equalsIgnoreCase(userFlashcard.getTranslation().trim());
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
}
