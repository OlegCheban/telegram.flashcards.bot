package bot.botapi.handlers.learn.exercises;

import bot.FlashcardsBotTestAbstract;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class StopLearningMessageHandlerTest extends FlashcardsBotTestAbstract {

    @Test
    @Override
    protected void test() throws Exception {
        when(message.getChatId()).thenReturn(0L);
        when(message.getText()).thenReturn(STOP_LEARNING);
        when(dataLayer.isLearnFlashcardState(message.getChatId())).thenReturn(true);
        List<BotApiMethod<?>> list = (List<BotApiMethod<?>>) handleMessageInputMethod().invoke(testBot, message);

        assertEquals("Keep learning!", ((SendMessage) list.get(0)).getText());
    }
}
