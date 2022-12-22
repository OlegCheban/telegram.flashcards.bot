package bot.botapi;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.flashcards.telegram.bot.botapi.CallbackFactory;
import ru.flashcards.telegram.bot.botapi.MessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.examples.FlashcardUsageExamplesCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.*;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.BoostPriorityCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.ReturnToLearnSwiperCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.SwiperRefreshFlashcardCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.translation.TranslateFlashcardCallbackHandler;
import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.*;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class CallbackFactoryTest {
    @WeldSetup
    private WeldInitiator weld =
            WeldInitiator
                    .from(CallbackFactory.class, CallbackFactoryTest.class)
                    .activate(RequestScoped.class)
                    .build();

    @Produces
    TranslateFlashcardCallbackHandler produceTranslateFlashcardCallbackHandler(){
        return Mockito.mock(TranslateFlashcardCallbackHandler.class);
    }
    @Produces
    AddToLearnCallbackHandler produceAddToLearnCallbackHandler(){
        return Mockito.mock(AddToLearnCallbackHandler.class);
    }
    @Produces
    AddToLearnAndNextCallbackHandler produceAddToLearnAndNextCallbackHandler(){
        return Mockito.mock(AddToLearnAndNextCallbackHandler.class);
    }
    @Produces
    ProceedToRepetitionCallbackHandler produceProceedToRepetitionCallbackHandler(){
        return Mockito.mock(ProceedToRepetitionCallbackHandler.class);
    }
    @Produces
    ReturnToLearnSwiperCallbackHandler produceReturnToLearnSwiperCallbackHandler(){
        return Mockito.mock(ReturnToLearnSwiperCallbackHandler.class);
    }
    @Produces
    ReturnToLearnCallbackHandler produceReturnToLearnCallbackHandler(){
        return Mockito.mock(ReturnToLearnCallbackHandler.class);
    }
    @Produces
    BoostPriorityCallbackHandler produceBoostPriorityCallbackHandler(){
        return Mockito.mock(BoostPriorityCallbackHandler.class);
    }
    @Produces
    ExcludeCallbackHandler produceExcludeCallbackHandler(){
        return Mockito.mock(ExcludeCallbackHandler.class);
    }
    @Produces
    ExcludeAndNextCallbackHandler produceExcludeAndNextCallbackHandler(){
        return Mockito.mock(ExcludeAndNextCallbackHandler.class);
    }
    @Produces
    SwiperRefreshFlashcardCallbackHandler produceSwiperRefreshFlashcardCallbackHandler(){
        return Mockito.mock(SwiperRefreshFlashcardCallbackHandler.class);
    }
    @Produces
    FlashcardUsageExamplesCallbackHandler produceFlashcardUsageExamplesCallbackHandler(){
        return Mockito.mock(FlashcardUsageExamplesCallbackHandler.class);
    }
    @Produces
    QuiteCallbackHandler produceQuiteCallbackHandler(){
        return Mockito.mock(QuiteCallbackHandler.class);
    }
    @Produces
    DisableExerciseMessageHandler produceDisableExerciseMessageHandler(){
        return Mockito.mock(DisableExerciseMessageHandler.class);
    }
    @Produces
    EnableExerciseMessageHandler produceEnableExerciseMessageHandler(){
        return Mockito.mock(EnableExerciseMessageHandler.class);
    }

    @Mock
    private CallbackData callbackData;

    @Test
    void testTranslateFlashcardCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(TRANSLATE);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(TranslateFlashcardCallbackHandler.class));
    }

    @Test
    void testAddToLearnCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(ADD_TO_LEARN);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(AddToLearnCallbackHandler.class));
    }

    @Test
    void testAddToLearnAndNextCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(ADD_TO_LEARN_AND_NEXT);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(AddToLearnAndNextCallbackHandler.class));
    }

    @Test
    void testProceedToRepetitionCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(PROCEED_LEARNING);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(ProceedToRepetitionCallbackHandler.class));
    }

    @Test
    void testReturnToLearnSwiperCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(SWIPER_RETURN_TO_LEARN);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(ReturnToLearnSwiperCallbackHandler.class));
    }

    @Test
    void testBoostPriorityCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(BOOST_PRIORITY);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(BoostPriorityCallbackHandler.class));
    }

    @Test
    void testExcludeCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(EXCLUDE);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(ExcludeCallbackHandler.class));
    }

    @Test
    void testExcludeAndNextCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(EXCLUDE_AND_NEXT);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(ExcludeAndNextCallbackHandler.class));
    }

    @Test
    void testSwiperRefreshFlashcardCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(SWIPER_PREV);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(SwiperRefreshFlashcardCallbackHandler.class));

        when(callbackData.getCommand()).thenReturn(SWIPER_NEXT);
        handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(SwiperRefreshFlashcardCallbackHandler.class));
    }

    @Test
    void testFlashcardUsageExamplesCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(EXAMPLES);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(FlashcardUsageExamplesCallbackHandler.class));
    }

    @Test
    void testQuiteCallbackHandler(){
        when(callbackData.getCommand()).thenReturn(QUITE);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(QuiteCallbackHandler.class));
    }

    @Test
    void testDisableExerciseMessageHandler(){
        when(callbackData.getCommand()).thenReturn(DISABLE_EXCERCISE);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(DisableExerciseMessageHandler.class));
    }

    @Test
    void testEnableExerciseMessageHandler(){
        when(callbackData.getCommand()).thenReturn(ENABLE_EXCERCISE);
        MessageHandler<CallbackQuery> handler = weld.select(CallbackFactory.class).get().getHandler(callbackData);
        assertThat(handler, instanceOf(EnableExerciseMessageHandler.class));
    }
}
