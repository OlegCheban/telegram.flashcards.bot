package ru.flashcards.telegram.bot.botapi.swiper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;

import java.util.ArrayList;
import java.util.List;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class Swiper {
    private String charCond = "";
    private ObjectMapper objectMapper = new ObjectMapper();
    private SwiperFlashcard swiperFlashcard;

    public Swiper(String charCond, SwiperFlashcard flashcard) {
        this.charCond = charCond;
        this.swiperFlashcard = flashcard;
    }

    public InlineKeyboardMarkup getSwiperKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> swiperRowInline = new ArrayList<>();
        List<InlineKeyboardButton> optionsRowInline = new ArrayList<>();

        try {
            if (swiperFlashcard.getPrevId() != 0) {
                swiperRowInline.add(prevButton());
            }
            if (swiperFlashcard.getNextId() != 0) {
                swiperRowInline.add(nextButton());
            }
            if (swiperFlashcard.getLearnPrc() == 100){
                optionsRowInline.add(returnToLearnButton());
            }
            if (swiperFlashcard.getLearnPrc() == 0 && swiperFlashcard.getNearestTraining() == 0){
                optionsRowInline.add(boostPriorityButton());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        rowsInline.add(optionsRowInline);
        rowsInline.add(swiperRowInline);
        markupInline.setKeyboard(rowsInline);
        return  markupInline;
    }

    private InlineKeyboardButton prevButton() throws JsonProcessingException {
        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("prev");
        CallbackData prev = new CallbackData(SWIPER_PREV);
        prev.setEntityId(swiperFlashcard.getPrevId());

        if (charCond != null){
            SwiperParams swiperParams = new SwiperParams(charCond);
            prev.setSwiper(swiperParams);
        }
        prevButton.setCallbackData(objectMapper.writeValueAsString(prev));

        return prevButton;
    }

    private InlineKeyboardButton nextButton() throws JsonProcessingException {
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("next");
        CallbackData next = new CallbackData(SWIPER_NEXT);
        next.setEntityId(swiperFlashcard.getNextId());

        if (charCond != null){
            SwiperParams swiperParams = new SwiperParams(charCond);
            next.setSwiper(swiperParams);
        }
        nextButton.setCallbackData(objectMapper.writeValueAsString(next));

        return nextButton;
    }

    private InlineKeyboardButton returnToLearnButton() throws JsonProcessingException {
        InlineKeyboardButton returnToLearnButton = new InlineKeyboardButton();
        returnToLearnButton.setText("reset progress");
        CallbackData returnToLearnCallbackData = new CallbackData(SWIPER_RETURN_TO_LEARN);
        returnToLearnCallbackData.setEntityId(swiperFlashcard.getCurrentId());

        if (charCond != null){
            SwiperParams swiperParams = new SwiperParams(charCond);
            returnToLearnCallbackData.setSwiper(swiperParams);
        }
        returnToLearnButton.setCallbackData(objectMapper.writeValueAsString(returnToLearnCallbackData));
        return returnToLearnButton;
    }

    private InlineKeyboardButton boostPriorityButton() throws JsonProcessingException {
        InlineKeyboardButton boostPriorityButton = new InlineKeyboardButton();
        boostPriorityButton.setText("boost priority");

        CallbackData boostPriorityCallbackData = new CallbackData(BOOST_PRIORITY);
        boostPriorityCallbackData.setEntityId(swiperFlashcard.getCurrentId());

        if (charCond != null){
            SwiperParams swiperParams = new SwiperParams(charCond);
            boostPriorityCallbackData.setSwiper(swiperParams);
        }

        boostPriorityButton.setCallbackData(objectMapper.writeValueAsString(boostPriorityCallbackData));
        return boostPriorityButton;
    }
}
