package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.wateringSession.CheckWateringSessionExerciseMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.wateringSession.StopWateringSessionHandler;

import javax.inject.Inject;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class WateringSessionMessageFactory implements MessageHandlerAbstractFactory<MessageHandler<Message>> {
    @Inject
    private StopWateringSessionHandler stopWateringSessionHandler;

    @Inject
    private CheckWateringSessionExerciseMessageHandler checkWateringSessionExerciseMessageHandler;

    @Override
    public MessageHandler<Message> getHandler(Message message) {
        if (message.getText().equals(STOP_LEARNING)){
            return stopWateringSessionHandler;
        } else {
            return checkWateringSessionExerciseMessageHandler;
        }
    }
}
