package ru.flashcards.telegram.bot.botapi.handlers.learn;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.InputMessageCallbackHandler;
import ru.flashcards.telegram.bot.command.addToLearn.SuggestFlashcard;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.Flashcard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class ExcludeAndNextCallbackHandler implements InputMessageCallbackHandler {
    private CallbackData callbackData;
    private SuggestFlashcard suggestFlashcard;
    private DataLayerObject dataLayer;

    public ExcludeAndNextCallbackHandler(CallbackData callbackData, DataLayerObject dataLayerObject) {
        this.callbackData = callbackData;
        this.dataLayer = dataLayerObject;
        this.suggestFlashcard = new SuggestFlashcard(dataLayerObject);
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();
        Long flashcardId = callbackData.getEntityId();

        Flashcard flashcard = dataLayer.findFlashcardById(flashcardId);
        dataLayer.exceptFlashcard(chatId, flashcardId);

        EditMessageText translationMessage = new EditMessageText();
        translationMessage.setChatId(String.valueOf(chatId));
        translationMessage.setMessageId(toIntExact(messageId));
        translationMessage.enableMarkdown(true);
        translationMessage.setText("Flashcard *" + flashcard.getWord() + "* excluded");

        list.add(translationMessage);
        suggestFlashcard.byTop3000Category(chatId);
        return list;
    }
}
