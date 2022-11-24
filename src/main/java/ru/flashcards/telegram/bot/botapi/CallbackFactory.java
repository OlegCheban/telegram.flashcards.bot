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

import javax.inject.Inject;
import java.util.Collections;

import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class CallbackFactory implements CallbackHandlerAbstractFactory<MessageHandler<CallbackQuery>> {
    @Inject
    TranslateFlashcardCallbackHandler translateFlashcardCallbackHandler;
    @Inject
    AddToLearnCallbackHandler addToLearnCallbackHandler;
    @Inject
    AddToLearnAndNextCallbackHandler addToLearnAndNextCallbackHandler;
    @Inject
    ProceedToRepetitionCallbackHandler proceedToRepetitionCallbackHandler;
    @Inject
    ReturnToLearnSwiperCallbackHandler returnToLearnSwiperCallbackHandler;
    @Inject
    ReturnToLearnCallbackHandler returnToLearnCallbackHandler;
    @Inject
    BoostPriorityCallbackHandler boostPriorityCallbackHandler;
    @Inject
    ExcludeCallbackHandler excludeCallbackHandler;
    @Inject
    ExcludeAndNextCallbackHandler excludeAndNextCallbackHandler;
    @Inject
    SwiperRefreshFlashcardCallbackHandler swiperRefreshFlashcardCallbackHandler;
    @Inject
    FlashcardUsageExamplesCallbackHandler flashcardUsageExamplesCallbackHandler;
    @Inject
    QuiteCallbackHandler quiteCallbackHandler;
    @Inject
    DisableExcerciseMessageHandler disableExcerciseMessageHandler;
    @Inject
    EnableExcerciseMessageHandler enableExcerciseMessageHandler;

    @Override
    public MessageHandler<CallbackQuery> getHandler(String callbackDataJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callback = null;
        try {
            callback = objectMapper.readValue(callbackDataJson, CallbackData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        switch (callback.getCommand()){
            case TRANSLATE:
                return translateFlashcardCallbackHandler;
            case ADD_TO_LEARN:
                return addToLearnCallbackHandler;
            case ADD_TO_LEARN_AND_NEXT:
                return addToLearnAndNextCallbackHandler;
            case PROCEED_LEARNING:
                return proceedToRepetitionCallbackHandler;
            case SWIPER_RETURN_TO_LEARN:
                return returnToLearnSwiperCallbackHandler;
            case RETURN_TO_LEARN:
                return returnToLearnCallbackHandler;
            case BOOST_PRIORITY:
                return boostPriorityCallbackHandler;
            case EXCLUDE:
                return excludeCallbackHandler;
            case EXCLUDE_AND_NEXT:
                return excludeAndNextCallbackHandler;
            case SWIPER_PREV:
            case SWIPER_NEXT:
                return swiperRefreshFlashcardCallbackHandler;
            case EXAMPLES:
                return flashcardUsageExamplesCallbackHandler;
            case QUITE:
                return quiteCallbackHandler;
            case DISABLE_EXCERCISE:
                return disableExcerciseMessageHandler;
            case ENABLE_EXCERCISE:
                return enableExcerciseMessageHandler;
        }

        return m -> Collections.emptyList();
    }
}
