package ru.flashcards.telegram.bot.botapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.flashcards.telegram.bot.botapi.handlers.examples.FlashcardUsageExamplesCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.*;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.BoostPriorityCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.ReturnToLearnSwiperCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.SwiperRefreshFlashcardCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.translate.TranslateFlashcardCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import java.util.Collections;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class CallbackFactory implements CallbackHandlerAbstractFactory<MessageHandler<CallbackQuery>> {
    @Override
    public MessageHandler<CallbackQuery> getHandler(String callbackDataJson, DataLayerObject dataLayer) {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callback = null;
        try {
            callback = objectMapper.readValue(callbackDataJson, CallbackData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        switch (callback.getCommand()){
            case TRANSLATE:
                return new TranslateFlashcardCallbackHandler(callback, dataLayer);
            case ADD_TO_LEARN:
                return new AddToLearnCallbackHandler(callback, dataLayer);
            case ADD_TO_LEARN_AND_NEXT:
                return new AddToLearnAndNextCallbackHandler(callback, dataLayer);
            case PROCEED_LEARNING:
                return new ProceedToRepetitionCallbackHandler(callback, dataLayer);
            case SWIPER_RETURN_TO_LEARN:
                return new ReturnToLearnSwiperCallbackHandler(callback, dataLayer);
            case RETURN_TO_LEARN:
                return new ReturnToLearnCallbackHandler(callback, dataLayer);
            case BOOST_PRIORITY:
                return new BoostPriorityCallbackHandler(callback, dataLayer);
            case EXCLUDE:
                return new ExcludeCallbackHandler(callback, dataLayer);
            case EXCLUDE_AND_NEXT:
                return new ExcludeAndNextCallbackHandler(callback, dataLayer);
            case SWIPER_PREV:
            case SWIPER_NEXT:
                return new SwiperRefreshFlashcardCallbackHandler(callback, dataLayer);
            case EXAMPLES:
                return new FlashcardUsageExamplesCallbackHandler(callback, dataLayer);
            case QUITE:
                return new QuiteCallbackHandler(callback, dataLayer);
            case DISABLE_EXCERCISE:
                return new DisableExcerciseMessageHandler(callback, dataLayer);
            case ENABLE_EXCERCISE:
                return new EnableExcerciseMessageHandler(callback, dataLayer);
        }

        return m -> Collections.emptyList();
    }
}
