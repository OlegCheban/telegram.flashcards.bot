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
import ru.flashcards.telegram.bot.botapi.handlers.learn.QuiteCallbackHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuiteMessageHandlerTest {
    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private CallbackData callbackData;

    @Test
    void test() {
        when(message.getChatId()).thenReturn(0L);
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getMessage()).thenReturn(message);

        QuiteCallbackHandler handler = new QuiteCallbackHandler(callbackData);
        List<BotApiMethod<?>> list = Mockito.spy(handler).handle(callbackQuery);

        assertEquals("Done", ((EditMessageText) list.get(0)).getText());
    }
}
