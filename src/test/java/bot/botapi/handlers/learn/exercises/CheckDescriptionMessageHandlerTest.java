package bot.botapi.handlers.learn.exercises;

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
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.utils.RandomMessageText;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.CHECK_DESCRIPTION;

@ExtendWith(MockitoExtension.class)
public class CheckDescriptionMessageHandlerTest {
    @Mock
    private Message message;

    @Mock
    private ExerciseDataHandler exerciseDataHandler;

    @Spy
    private ExerciseMessageHandlerFactory exerciseMessageHandlerFactory;

    @Test
    void test() {
        when(message.getChatId()).thenReturn(0L);
        when(message.getText()).thenReturn("checkDescriptionValue");

        ExerciseFlashcard exerciseFlashcard =
                new ExerciseFlashcard(
                        0L,
                        null,
                        CHECK_DESCRIPTION,
                        "checkDescriptionValue",
                        null,
                        0L,
                        null,
                        null);

        when(exerciseDataHandler.getCurrentExercise(message.getChatId())).thenReturn(exerciseFlashcard);

        InputMessageHandler inputMessageHandler = exerciseMessageHandlerFactory.getHandler(message, exerciseDataHandler);
        List<BotApiMethod<?>> list = inputMessageHandler.handle(message);

        assertTrue(RandomMessageText.positiveMessages.contains(((SendMessage) list.get(0)).getText()));
    }
}
