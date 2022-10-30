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
import ru.flashcards.telegram.bot.botapi.handlers.learn.EnableExcerciseMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnableExcerciseMessageHandlerTest {
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
        when(message.getChatId()).thenReturn(0L);
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getMessage()).thenReturn(message);

        EnableExcerciseMessageHandler handler = new EnableExcerciseMessageHandler(callbackData);
        Field field = handler.getClass().getDeclaredField("exerciseDataHandler");
        field.setAccessible(true);
        field.set(handler, exerciseDataHandler);
        List<BotApiMethod<?>> list = Mockito.spy(handler).handle(callbackQuery);

        assertEquals("Done", ((EditMessageText) list.get(0)).getText());
    }
}
