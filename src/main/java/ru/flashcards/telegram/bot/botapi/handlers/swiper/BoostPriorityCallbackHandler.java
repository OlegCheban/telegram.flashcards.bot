package ru.flashcards.telegram.bot.botapi.handlers.swiper;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.InputMessageCallbackHandler;
import ru.flashcards.telegram.bot.botapi.swiper.Swiper;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.SwiperDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class BoostPriorityCallbackHandler implements InputMessageCallbackHandler {
    private CallbackData callbackData;
    private SwiperDataHandler swiperDataHandler = new SwiperDataHandler();
    private ExerciseDataHandler exerciseDataHandler = new ExerciseDataHandler();

    public BoostPriorityCallbackHandler(CallbackData callbackData) {
        this.callbackData = callbackData;
    }

    @Override
    public List<BotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        String characterCondition = null;
        List<BotApiMethod<?>> list = new ArrayList<>();
        Message message = callbackQuery.getMessage();
        long messageId = message.getMessageId();
        long chatId = message.getChatId();
        Long userFlashcardId = callbackData.getEntityId();

        exerciseDataHandler.boostUserFlashcardPriority(userFlashcardId);

        if (callbackData.getSwiper() != null){
            characterCondition = callbackData.getSwiper().getCharCond();
        }
        SwiperFlashcard swiperFlashcard = swiperDataHandler.getSwiperFlashcard(chatId, callbackData.getEntityId(), characterCondition);

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
        resultMessage.setText("*" + swiperFlashcard.getWord() + "* added to nearest training");

        list.add(resultMessage);

        return list;
    }
}