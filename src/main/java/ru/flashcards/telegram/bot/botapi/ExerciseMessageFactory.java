package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.*;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;

import javax.inject.Inject;
import java.util.Collections;

public class ExerciseMessageFactory implements MessageHandlerAbstractFactory<MessageHandler<Message>> {
    @Inject
    private MemorisedMessageHandler memorisedMessageHandler;
    @Inject
    private StopLearningMessageHandler stopLearningMessageHandler;
    @Inject
    private CheckDescriptionMessageHandler checkDescriptionMessageHandler;
    @Inject
    private CheckTranslationMessageHandler checkTranslationMessageHandler;
    @Inject
    private CheckSpellingMessageHandler checkSpellingMessageHandler;
    @Inject
    private CompleteTheGapsMessageHandler completeTheGapsMessageHandler;
    @Inject
    private DataLayerObject dataLayer;

    @Override
    public MessageHandler<Message> getHandler(Message message) {
        if (message.getText().equals(BotCommand.STOP_LEARNING.command)){
            return stopLearningMessageHandler;
        } else {
            ExerciseFlashcard currentExercise = dataLayer.getCurrentExercise(message.getChatId());

            switch (currentExercise.getExerciseKindCode()){
                case MEMORISED:
                    return memorisedMessageHandler;
                case CHECK_DESCRIPTION:
                    return checkDescriptionMessageHandler;
                case CHECK_TRANSLATION:
                    return checkTranslationMessageHandler;
                case CHECK_SPELLING:
                case CHECK_SPELLING_WITH_HELPS:
                    return checkSpellingMessageHandler;
                case COMPLETE_THE_GAPS:
                    return completeTheGapsMessageHandler;
            }
        }

        return m -> Collections.emptyList();
    }
}
