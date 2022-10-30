package bot.botapi.handlers.learn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.learn.StopLearningMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StopLearningCallbackHandlerTest {
    @Mock
    private ExerciseDataHandler exerciseDataHandler;
    @Mock
    private Message message;

    @Test
    void test() {
        when(message.getChatId()).thenReturn(0L);
        StopLearningMessageHandler handler = new StopLearningMessageHandler(exerciseDataHandler);
        List<BotApiMethod<?>> list = Mockito.spy(handler).handle(message);
        assertEquals("Keep learning!", ((SendMessage) list.get(0)).getText());
    }
}
