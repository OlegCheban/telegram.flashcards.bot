package ru.flashcards.telegram.bot.botapi.handlers.swiper;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.botapi.swiper.Swiper;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class SwiperRefreshFlashcardCallbackHandler implements MessageHandler<CallbackQuery> {
    @Inject
    private DataLayerObject dataLayer;

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        CallbackData callbackData = getCallbackData(callbackQuery.getData());
        String characterCondition = null;
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();

        if (callbackData.getSwiper() != null){
            characterCondition = callbackData.getSwiper().getCharCond();
        }
        SwiperFlashcard swiperFlashcard = dataLayer.getSwiperFlashcard(chatId, callbackData.getEntityId(), characterCondition);

        EditMessageText nextMessage = new EditMessageText();
        nextMessage.setChatId(String.valueOf(chatId));
        nextMessage.setMessageId(toIntExact(messageId));
        nextMessage.enableMarkdown(true);
        nextMessage.setText("*" + swiperFlashcard.getWord() + "* \\[" + swiperFlashcard.getTranscription() + "] ("+swiperFlashcard.getLearnPrc()+"% learned)\n" +
                swiperFlashcard.getDescription() + "\n\n" + "*Translation:* " + swiperFlashcard.getTranslation()
        );

        Swiper swiper = new Swiper(characterCondition, swiperFlashcard);

        nextMessage.setReplyMarkup(swiper.getSwiperKeyboardMarkup());
        list.add(nextMessage);

        return list;
    }
}