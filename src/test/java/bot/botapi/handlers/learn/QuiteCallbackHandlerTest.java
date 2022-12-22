package bot.botapi.handlers.learn;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.learn.QuiteCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import javax.enterprise.inject.Produces;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class QuiteCallbackHandlerTest {
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private Message message;
    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(QuiteCallbackHandler.class, QuiteCallbackHandlerTest.class).build();

    @Test
    void test() {
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getMessage()).thenReturn(message);
        List<BotApiMethod<?>> list = weld.select(QuiteCallbackHandler.class).get().handle(callbackQuery);

        assertEquals("Done", ((EditMessageText) list.get(0)).getText());
    }
}
