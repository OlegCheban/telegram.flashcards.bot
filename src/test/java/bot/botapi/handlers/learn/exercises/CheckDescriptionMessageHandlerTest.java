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
import static ru.flashcards.telegram.bot.botapi.Literals.CHECK_DESCRIPTION;

public class CheckDescriptionMessageHandlerTest extends FlashcardsBotTestAbstract {
    @Mock
    private ExerciseFlashcard exerciseFlashcard;

    @Test
    @Override
    protected void test() throws Exception{
        when(exerciseFlashcard.getExerciseCode()).thenReturn(CHECK_DESCRIPTION);
        when(exerciseFlashcard.getDescription()).thenReturn("descriptionValue");
        when(message.getChatId()).thenReturn(0L);
        when(message.getText()).thenReturn("descriptionValue");
        when(dataLayer.isLearnFlashcardState(message.getChatId())).thenReturn(true);
        when(dataLayer.getCurrentExercise(message.getChatId())).thenReturn(exerciseFlashcard);
        when(dataLayer.getExercise(message.getChatId())).thenReturn(exerciseFlashcard);
        List<BotApiMethod<?>> list = (List<BotApiMethod<?>>) handleMessageInputMethod().invoke(testBot, message);

        assertTrue(RandomMessageText.positiveMessages.contains(((SendMessage) list.get(0)).getText()));
    }
}
