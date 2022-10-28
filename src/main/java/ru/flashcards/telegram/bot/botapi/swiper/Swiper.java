package ru.flashcards.telegram.bot.botapi.swiper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.flashcards.telegram.bot.botapi.CallbackData;

import java.util.ArrayList;
import java.util.List;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class Swiper {
    private String charCond = "";
    private Long currentId;
    private Long prevId;
    private Long nextId;
    private ObjectMapper objectMapper = new ObjectMapper();
    private int learnPrc;

    public Swiper(String charCond, Long prevId, Long nextId, Long currentId, int learnPrc) {
        this.charCond = charCond;
        this.prevId = prevId;
        this.nextId = nextId;
        this.learnPrc = learnPrc;
        this.currentId = currentId;
    }

    public InlineKeyboardMarkup getSwiperMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        try {
            if (prevId != 0) {
                rowInline.add(prevButton());
            }
            if (nextId != 0) {
                rowInline.add(nextButton());
            }
            if (learnPrc == 100){
                List<InlineKeyboardButton> returnToLearnRowInline = new ArrayList<>();
                returnToLearnRowInline.add(returnToLearnButton());
                rowsInline.add(returnToLearnRowInline);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        return  markupInline;
    }

    private InlineKeyboardButton prevButton() throws JsonProcessingException {
        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("prev");
        CallbackData prev = new CallbackData(SWIPER_PREV);
        prev.setEntityId(prevId);

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
        next.setEntityId(nextId);

        if (charCond != null){
            SwiperParams swiperParams = new SwiperParams(charCond);
            next.setSwiper(swiperParams);
        }
        nextButton.setCallbackData(objectMapper.writeValueAsString(next));

        return nextButton;
    }

    private InlineKeyboardButton returnToLearnButton(){
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("return to learn");

        CallbackData callbackData = new CallbackData(RETURN_TO_LEARN);
        callbackData.setEntityId(currentId);

        try {
            nextButton.setCallbackData(objectMapper.writeValueAsString(callbackData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return nextButton;
    }
}
