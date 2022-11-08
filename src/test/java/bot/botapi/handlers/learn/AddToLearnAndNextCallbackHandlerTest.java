package bot.botapi.handlers.learn;

import bot.FlashcardsBotTestAbstract;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.db.dmlOps.dto.Flashcard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.ADD_TO_LEARN_AND_NEXT;

public class AddToLearnAndNextCallbackHandlerTest extends FlashcardsBotTestAbstract {
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private Flashcard flashcard;

    @Test
    @Override
    protected void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callbackData = new CallbackData(ADD_TO_LEARN_AND_NEXT);
        callbackData.setEntityId(0L);

        when(flashcard.getWord()).thenReturn("word");
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getData()).thenReturn(objectMapper.writeValueAsString(callbackData));
        when(callbackQuery.getMessage()).thenReturn(message);
        when(dataLayer.findFlashcardById(0L)).thenReturn(flashcard);

        List<BotApiMethod<?>> list = (List<BotApiMethod<?>>) handleCallbackQueryInputMethod().invoke(testBot, callbackQuery);

        assertEquals("Flashcard *word* added for learning", ((EditMessageText) list.get(0)).getText());
    }
}
