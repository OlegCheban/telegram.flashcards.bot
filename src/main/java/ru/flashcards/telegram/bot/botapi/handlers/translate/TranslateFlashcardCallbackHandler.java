package ru.flashcards.telegram.bot.botapi.handlers.translate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.InputMessageCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.FlashcardDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;
import static ru.flashcards.telegram.bot.botapi.Literals.EXAMPLES;

public class TranslateFlashcardCallbackHandler implements InputMessageCallbackHandler {
    private CallbackData callbackData;
    private ObjectMapper objectMapper = new ObjectMapper();
    private FlashcardDataHandler FlashcardDataHandler = new FlashcardDataHandler();
    private final String pushpinEmoji = "\uD83D\uDCCC";

    public TranslateFlashcardCallbackHandler(CallbackData callbackData) {
        this.callbackData = callbackData;
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();
        Long userFlashcardId = callbackData.getEntityId();
        UserFlashcard flashcard = FlashcardDataHandler.findUserFlashcardById(userFlashcardId);

        EditMessageText translationMessage = new EditMessageText();
        translationMessage.setChatId(String.valueOf(chatId));
        translationMessage.setMessageId(toIntExact(messageId));
        translationMessage.enableMarkdown(true);

        translationMessage.setText("*" + flashcard.getWord() + "* \\[" + flashcard.getTranscription() + "] " + pushpinEmoji + " \n"+flashcard.getDescription() + "\n\n"+flashcard.getTranslation());

        CallbackData callbackData = new CallbackData(EXAMPLES);
        callbackData.setEntityId(userFlashcardId);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("example of usage");
        try {
            button.setCallbackData(objectMapper.writeValueAsString(callbackData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        translationMessage.setReplyMarkup(markupInline);
        list.add(translationMessage);

        return list;
    }
}