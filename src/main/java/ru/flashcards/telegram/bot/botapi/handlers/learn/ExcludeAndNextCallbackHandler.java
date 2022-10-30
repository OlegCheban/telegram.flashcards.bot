package ru.flashcards.telegram.bot.botapi.handlers.learn;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.InputMessageCallbackHandler;
import ru.flashcards.telegram.bot.command.addToLearn.SuggetFlashcard;
import ru.flashcards.telegram.bot.db.dmlOps.FlashcardDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.Flashcard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class ExcludeAndNextCallbackHandler implements InputMessageCallbackHandler {
    private CallbackData callbackData;
    private FlashcardDataHandler flashcardDataHandler = new FlashcardDataHandler();
    private SuggetFlashcard suggetFlashcard = new SuggetFlashcard();

    public ExcludeAndNextCallbackHandler(CallbackData callbackData) {
        this.callbackData = callbackData;
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();
        Long flashcardId = callbackData.getEntityId();

        Flashcard flashcard = flashcardDataHandler.findFlashcardById(flashcardId);
        flashcardDataHandler.exceptFlashcard(chatId, flashcardId);

        EditMessageText translationMessage = new EditMessageText();
        translationMessage.setChatId(String.valueOf(chatId));
        translationMessage.setMessageId(toIntExact(messageId));
        translationMessage.enableMarkdown(true);
        translationMessage.setText("Flashcard *" + flashcard.getWord() + "* excluded");

        list.add(translationMessage);
        suggetFlashcard.byTop3000Category(chatId);
        return list;
    }
}
