package bot.botapi.handlers.swiper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.BoostPriorityCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.SwiperDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoostPriorityCallbackHandlerTest {
    @Mock
    private SwiperDataHandler swiperDataHandler;
    @Mock
    private ExerciseDataHandler exerciseDataHandler;
    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private CallbackData callbackData;

    @Test
    void test() throws NoSuchFieldException, IllegalAccessException {
        SwiperFlashcard swiperFlashcard = new SwiperFlashcard(0L,0L,0L, "word", "description", "translation", "transcription", 0, 0);

        when(message.getChatId()).thenReturn(0L);
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(callbackData.getEntityId()).thenReturn(0L);
        when(swiperDataHandler.getSwiperFlashcard(0L,0L,null)).thenReturn(swiperFlashcard);

        BoostPriorityCallbackHandler handler = new BoostPriorityCallbackHandler(callbackData);

        Field spacedRepetitionNotificationDataHandlerField = handler.getClass().getDeclaredField("swiperDataHandler");
        spacedRepetitionNotificationDataHandlerField.setAccessible(true);
        spacedRepetitionNotificationDataHandlerField.set(handler, swiperDataHandler);

        Field exerciseDataHandlerField = handler.getClass().getDeclaredField("exerciseDataHandler");
        exerciseDataHandlerField.setAccessible(true);
        exerciseDataHandlerField.set(handler, exerciseDataHandler);

        List<BotApiMethod<?>> list = Mockito.spy(handler).handle(callbackQuery);

        assertEquals("*word* added to nearest training", ((SendMessage) list.get(1)).getText());
    }
}
