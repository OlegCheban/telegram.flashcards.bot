package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.*;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;

import java.util.Collections;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class ExerciseMessageFactory implements MessageHandlerAbstractFactory<MessageHandler<Message>> {
    @Override
    public MessageHandler<Message> getHandler(Message message, DataLayerObject dataLayer) {
        if (message.getText().equals(STOP_LEARNING)){
            return new StopLearningMessageHandler(dataLayer);
        } else {
            ExerciseFlashcard currentExercise = dataLayer.getCurrentExercise(message.getChatId());

            switch (currentExercise.getExerciseCode()){
                case MEMORISED:
                    return new MemorisedMessageHandler(currentExercise, dataLayer);
                case CHECK_DESCRIPTION:
                    return new CheckDescriptionMessageHandler(currentExercise, dataLayer);
                case CHECK_TRANSLATION:
                    return new CheckTranslationMessageHandler(currentExercise, dataLayer);
                case CHECK_SPELLING:
                case CHECK_SPELLING_WITH_HELPS:
                    return new CheckSpellingMessageHandler(currentExercise, dataLayer);
                case COMPLETE_THE_GAPS:
                    return new CompleteTheGapsMessageHandler(currentExercise, dataLayer);
            }
        }

        return m -> Collections.emptyList();
    }
}
