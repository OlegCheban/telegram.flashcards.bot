package bot.botapi.handlers.learn;

import bot.FlashcardsBotTestAbstract;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.QUITE;

public class QuiteMessageHandlerTest extends FlashcardsBotTestAbstract {
    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;

    @Test
    @Override
    protected void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callbackData = new CallbackData(QUITE);
        callbackData.setEntityId(0L);

        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getData()).thenReturn(objectMapper.writeValueAsString(callbackData));
        when(callbackQuery.getMessage()).thenReturn(message);

        List<BotApiMethod<?>> list = (List<BotApiMethod<?>>) handleCallbackQueryInputMethod().invoke(testBot, callbackQuery);

        assertEquals("Done", ((EditMessageText) list.get(0)).getText());
    }
}
