package bot.botapi.handlers.learn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.ExerciseMessageHandlerFactory;
import ru.flashcards.telegram.bot.botapi.InputMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.STOP_LEARNING;

@ExtendWith(MockitoExtension.class)
public class StopLearningMessageHandlerTest {
    @Mock
    private Message message;

    @Mock
    private ExerciseDataHandler exerciseDataHandler;

    @Spy
    private ExerciseMessageHandlerFactory exerciseMessageHandlerFactory;

    @Test
    void test() {
        when(message.getChatId()).thenReturn(0L);
        when(message.getText()).thenReturn(STOP_LEARNING);

        InputMessageHandler inputMessageHandler = exerciseMessageHandlerFactory.getHandler(message, exerciseDataHandler);
        List<BotApiMethod<?>> list = inputMessageHandler.handle(message);

        assertEquals("Keep learning!", ((SendMessage) list.get(0)).getText());
    }
}
