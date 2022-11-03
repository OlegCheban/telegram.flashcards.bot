package ru.flashcards.telegram.bot.botapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.flashcards.telegram.bot.botapi.handlers.examples.FlashcardUsageExamplesCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.*;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.BoostPriorityCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.ReturnToLearnSwiperCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.SwiperRefreshFlashcardCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.translate.TranslateFlashcardCallbackHandler;

import java.util.Collections;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class CallbackHandlerFactory {
    public InputMessageCallbackHandler getHandler(String callbackDataJson){
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callback = null;
        try {
            callback = objectMapper.readValue(callbackDataJson, CallbackData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        switch (callback.getCommand()){
            case TRANSLATE:
                return new TranslateFlashcardCallbackHandler(callback);
            case ADD_TO_LEARN:
                return new AddToLearnCallbackHandler(callback);
            case ADD_TO_LEARN_AND_NEXT:
                return new AddToLearnAndNextCallbackHandler(callback);
            case PROCEED_LEARNING:
                return new ProceedToRepetitionCallbackHandler(callback);
            case SWIPER_RETURN_TO_LEARN:
                return new ReturnToLearnSwiperCallbackHandler(callback);
            case RETURN_TO_LEARN:
                return new ReturnToLearnCallbackHandler(callback);
            case BOOST_PRIORITY:
                return new BoostPriorityCallbackHandler(callback);
            case EXCLUDE:
                return new ExcludeCallbackHandler(callback);
            case EXCLUDE_AND_NEXT:
                return new ExcludeAndNextCallbackHandler(callback);
            case SWIPER_PREV:
            case SWIPER_NEXT:
                return new SwiperRefreshFlashcardCallbackHandler(callback);
            case EXAMPLES:
                return new FlashcardUsageExamplesCallbackHandler(callback);
            case QUITE:
                return new QuiteCallbackHandler(callback);
            case DISABLE_EXCERCISE:
                return new DisableExcerciseMessageHandler(callback);
            case ENABLE_EXCERCISE:
                return new EnableExcerciseMessageHandler(callback);
        }

        return m -> Collections.emptyList();
    }
}
