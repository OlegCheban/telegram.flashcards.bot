package ru.flashcards.telegram.bot.botapi.handlers.learn;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.InputMessageCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class ProceedToRepetitionCallbackHandler implements InputMessageCallbackHandler {
    private CallbackData callbackData;
    private DataLayerObject dataLayer;

    public ProceedToRepetitionCallbackHandler(CallbackData callbackData, DataLayerObject dataLayerObject) {
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

        EditMessageText translationMessage = new EditMessageText();
        translationMessage.setChatId(String.valueOf(chatId));
        translationMessage.setMessageId(toIntExact(messageId));
        translationMessage.enableMarkdown(true);
        translationMessage.setText("*Spaced repetition*\n*"+flashcard.getWord()+"* \\[" + flashcard.getTranscription() + "]\n\n"+flashcard.getDescription() + "\n\n"+flashcard.getTranslation());

        list.add(translationMessage);
        return list;
    }
}