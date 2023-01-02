package ru.flashcards.telegram.bot.botapi.handlers.swiper;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;
import ru.flashcards.telegram.bot.botapi.swiper.Swiper;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class RemoveFlashcardCallbackHandler implements MessageHandler<CallbackQuery> {
    @Inject
    private DataLayerObject dataLayer;

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        CallbackData callbackData = jsonToCallbackData(callbackQuery.getData());
        String characterCondition = null;
        String percentile = null;
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();
        Long userFlashcardId = callbackData.getEntityId();

        if (callbackData.getSwiper() != null){
            characterCondition = callbackData.getSwiper().getCharCond();
            percentile = callbackData.getSwiper().getPrc();
        }

        SwiperFlashcard swiperFlashcard =
                dataLayer.getSwiperFlashcard(
                        chatId,
                        callbackData.getEntityId(),
                        characterCondition,
                        percentile
                );

        dataLayer.deleteSpacedRepetitionHistory(userFlashcardId);
        dataLayer.deleteExerciseStat(userFlashcardId);
        dataLayer.removeFlashcard(userFlashcardId);

        EditMessageText formerMessage = new EditMessageText();
        formerMessage.setChatId(String.valueOf(chatId));
        formerMessage.setMessageId(toIntExact(messageId));
        formerMessage.enableMarkdown(true);

        if (swiperFlashcard.getPrevId() != 0 || swiperFlashcard.getNextId() != 0){
            swiperFlashcard =
                    dataLayer.getSwiperFlashcard(
                            chatId,
                            (swiperFlashcard.getNextId() == 0) ? swiperFlashcard.getPrevId() : swiperFlashcard.getNextId(),
                            characterCondition,
                            percentile
                    );

            formerMessage.setText("*" + swiperFlashcard.getWord() + "* /" + swiperFlashcard.getTranscription() + "/ (" +
                    swiperFlashcard.getLearnPrc()+"% learned)\n" + swiperFlashcard.getDescription() + "\n\n" +
                    "*Translation:* " + swiperFlashcard.getTranslation()
            );

            Swiper swiper = new Swiper(characterCondition, swiperFlashcard, percentile);
            formerMessage.setReplyMarkup(swiper.getSwiperKeyboardMarkup());
        } else {
            formerMessage.setText("The flashcard was removed");
        }
        list.add(formerMessage);

        return list;
    }
}
