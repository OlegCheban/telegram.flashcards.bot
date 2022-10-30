package bot.botapi.handlers.learn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.handlers.learn.AddToLearnCallbackHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.ExcludeCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.FlashcardDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.Flashcard;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExcludeCallbackHandlerTest {
    @Mock
    private FlashcardDataHandler flashcardDataHandler;
    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private CallbackData callbackData;

    @Test
    void test() throws NoSuchFieldException, IllegalAccessException {
        Flashcard flashcard = new Flashcard(0L, "description", "transcription", "translation", "word");

        when(message.getChatId()).thenReturn(0L);
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(flashcardDataHandler.findFlashcardById(0L)).thenReturn(flashcard);

        ExcludeCallbackHandler handler = new ExcludeCallbackHandler(callbackData);
        Field field = handler.getClass().getDeclaredField("flashcardDataHandler");
        field.setAccessible(true);
        field.set(handler, flashcardDataHandler);
        List<BotApiMethod<?>> list = Mockito.spy(handler).handle(callbackQuery);

        assertEquals("Flashcard *word* excluded", ((EditMessageText) list.get(0)).getText());
    }
}
