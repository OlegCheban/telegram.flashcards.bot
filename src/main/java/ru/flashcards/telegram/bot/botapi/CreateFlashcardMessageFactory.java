package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.create.CreateDefinitionMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.create.CreateFlashcardsMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.wateringSession.CheckWateringSessionExerciseMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.wateringSession.StopWateringSessionHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.util.Collections;

import static ru.flashcards.telegram.bot.botapi.Literals.STOP_LEARNING;

public class CreateFlashcardMessageFactory implements MessageHandlerAbstractFactory<MessageHandler<Message>> {
    @Override
    public MessageHandler<Message> getHandler(Message message, DataLayerObject dataLayer) {
        if (message.getText().contains("//")){
            return new CreateFlashcardsMessageHandler(dataLayer);
        }
        if (message.getText().contains("##")){
            return new CreateDefinitionMessageHandler(dataLayer);
        }

        return m -> Collections.emptyList();
    }
}
