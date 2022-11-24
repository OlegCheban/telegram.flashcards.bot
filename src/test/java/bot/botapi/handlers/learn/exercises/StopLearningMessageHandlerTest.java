package bot.botapi.handlers.learn.exercises;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.StopLearningMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import javax.enterprise.inject.Produces;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class StopLearningMessageHandlerTest {
    @Mock
    private Message message;

    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(StopLearningMessageHandler.class, StopLearningMessageHandlerTest.class).build();

    @Produces
    DataLayerObject dataLayer() {
        return  Mockito.mock(DataLayerObject.class);
    }

    @Test
    void test(){
        when(message.getChatId()).thenReturn(0L);
        List<BotApiMethod<?>> list = weld.select(StopLearningMessageHandler.class).get().handle(message);

        assertEquals("Keep learning!", ((SendMessage) list.get(0)).getText());
    }
}
