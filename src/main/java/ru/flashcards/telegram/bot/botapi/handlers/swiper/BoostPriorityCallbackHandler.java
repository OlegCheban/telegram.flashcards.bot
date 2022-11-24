package ru.flashcards.telegram.bot.botapi.handlers.swiper;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

public class BoostPriorityCallbackHandler implements MessageHandler<CallbackQuery> {
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
        Long userFlashcardId = callbackData.getEntityId();

        dataLayer.boostUserFlashcardPriority(userFlashcardId);

        if (callbackData.getSwiper() != null){
            characterCondition = callbackData.getSwiper().getCharCond();
        }
        SwiperFlashcard swiperFlashcard = dataLayer.getSwiperFlashcard(chatId, callbackData.getEntityId(), characterCondition);

        EditMessageText formerMessage = new EditMessageText();
        formerMessage.setChatId(String.valueOf(chatId));
        formerMessage.setMessageId(toIntExact(messageId));
        formerMessage.enableMarkdown(true);
        formerMessage.setText("*" + swiperFlashcard.getWord() + "* \\[" + swiperFlashcard.getTranscription() + "] ("+swiperFlashcard.getLearnPrc()+"% learned)\n" +
                swiperFlashcard.getDescription() + "\n\n" + "*Translation:* " + swiperFlashcard.getTranslation()
        );

        Swiper swiper = new Swiper(characterCondition, swiperFlashcard);
        formerMessage.setReplyMarkup(swiper.getSwiperKeyboardMarkup());
        list.add(formerMessage);

        SendMessage resultMessage = new SendMessage();
        resultMessage.setChatId(String.valueOf(message.getChatId()));
        resultMessage.enableMarkdown(true);
        resultMessage.setText("*" + swiperFlashcard.getWord() + "* added to next learning session");

        list.add(resultMessage);

        return list;
    }
}