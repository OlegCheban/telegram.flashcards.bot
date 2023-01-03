package ru.flashcards.telegram.bot.botapi.handlers.translation;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;
import static ru.flashcards.telegram.bot.botapi.BotKeyboardButton.EXAMPLES;

public class TranslateFlashcardCallbackHandler implements MessageHandler<CallbackQuery> {
    @Inject
    private DataLayerObject dataLayer;

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        CallbackData callbackData1 = jsonToCallbackData(callbackQuery.getData());
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();
        Long userFlashcardId = callbackData1.getEntityId();
        UserFlashcard flashcard = dataLayer.findUserFlashcardById(userFlashcardId);

        EditMessageText translationMessage = new EditMessageText();
        translationMessage.setChatId(String.valueOf(chatId));
        translationMessage.setMessageId(toIntExact(messageId));
        translationMessage.enableMarkdown(true);

        String pushpinEmoji = "\uD83D\uDCCC";
        translationMessage.setText("*" + flashcard.getWord() + "* /" + flashcard.getTranscription() + "/ " + pushpinEmoji + " \n"+flashcard.getDescription() + "\n\n"+flashcard.getTranslation());

        CallbackData callbackData = new CallbackData(EXAMPLES);
        callbackData.setEntityId(userFlashcardId);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("example of usage");
        button.setCallbackData(callbackDataToJson(callbackData));
        rowInline.add(button);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        translationMessage.setReplyMarkup(markupInline);
        list.add(translationMessage);

        return list;
    }
}