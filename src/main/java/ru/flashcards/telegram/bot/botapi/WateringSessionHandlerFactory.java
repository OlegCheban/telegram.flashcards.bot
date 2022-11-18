package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.wateringSession.CheckWateringSessionExcerciseMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.wateringSession.StopWateringSessionHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class WateringSessionHandlerFactory {
    public InputMessageHandler getHandler(Message message, DataLayerObject dataLayer){
        if (message.getText().equals(STOP_LEARNING)){
            return new StopWateringSessionHandler(dataLayer);
        } else {
            UserFlashcard userFlashcard = dataLayer.getUserFlashcardForWateringSession(message.getChatId());
            return new CheckWateringSessionExcerciseMessageHandler(userFlashcard, dataLayer);
        }
    }
}
