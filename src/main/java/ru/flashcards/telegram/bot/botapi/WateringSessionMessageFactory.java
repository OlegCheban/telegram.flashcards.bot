package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.*;
import ru.flashcards.telegram.bot.botapi.handlers.wateringSession.CheckWateringSessionExerciseMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.wateringSession.StopWateringSessionHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.util.Collections;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class WateringSessionMessageFactory implements MessageHandlerAbstractFactory<MessageHandler<Message>> {
    @Override
    public MessageHandler<Message> getHandler(Message message, DataLayerObject dataLayer) {
        if (message.getText().equals(STOP_LEARNING)){
            return new StopWateringSessionHandler(dataLayer);
        } else {
            UserFlashcard userFlashcard = dataLayer.getUserFlashcardForWateringSession(message.getChatId());
            return new CheckWateringSessionExerciseMessageHandler(userFlashcard, dataLayer);
        }
    }
}
