package ru.flashcards.telegram.bot.botapi.handlers.learn;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class ReturnToLearnCallbackHandler implements MessageHandler<CallbackQuery> {
    private CallbackData callbackData;
    private DataLayerObject dataLayer;

    public ReturnToLearnCallbackHandler(CallbackData callbackData, DataLayerObject dataLayerObject) {
        this.callbackData = callbackData;
        this.dataLayer = dataLayerObject;
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();
        Long userFlashcardId = callbackData.getEntityId();

        UserFlashcard flashcard = dataLayer.findUserFlashcardById(userFlashcardId);
        dataLayer.deleteSpacedRepetitionHistory(userFlashcardId);
        dataLayer.deleteExerciseStat(userFlashcardId);
        dataLayer.returnToLearn(userFlashcardId);

        EditMessageText resultMessage = new EditMessageText();
        resultMessage.setChatId(String.valueOf(chatId));
        resultMessage.setMessageId(toIntExact(messageId));
        resultMessage.enableMarkdown(true);
        resultMessage.setText("*Flashcard returned to learn*\n*"+flashcard.getWord()+"* \\[" + flashcard.getTranscription() + "]\n\n"+flashcard.getDescription() + "\n\n"+flashcard.getTranslation());

        list.add(resultMessage);
        return list;
    }
}