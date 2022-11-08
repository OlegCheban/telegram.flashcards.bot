package bot.botapi.handlers.learn.exercises;

import bot.FlashcardsBotTestAbstract;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.utils.RandomMessageText;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class MemorisedMessageHandlerTest extends FlashcardsBotTestAbstract {
    @Mock
    private ExerciseFlashcard exerciseFlashcard;

    @Test
    @Override
    protected void test() throws Exception{
        when(exerciseFlashcard.getExerciseCode()).thenReturn(MEMORISED);
        when(message.getChatId()).thenReturn(0L);
        when(message.getText()).thenReturn("Memorised");
        when(dataLayer.isLearnFlashcardState(message.getChatId())).thenReturn(true);
        when(dataLayer.getCurrentExercise(message.getChatId())).thenReturn(exerciseFlashcard);
        List<BotApiMethod<?>> list = (List<BotApiMethod<?>>) handleMessageInputMethod().invoke(testBot, message);

        assertTrue(RandomMessageText.positiveMessages.contains(((SendMessage) list.get(0)).getText()));
    }
}
