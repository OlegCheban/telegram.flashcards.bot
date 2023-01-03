package bot.botapi.handlers.learn;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;
import ru.flashcards.telegram.bot.botapi.handlers.learn.EnableExerciseMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import javax.enterprise.inject.Produces;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.BotKeyboardButton.ENABLE_EXCERCISE;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class EnableExerciseKindMessageHandlerTest {
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private Message message;

    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(EnableExerciseMessageHandler.class, EnableExerciseKindMessageHandlerTest.class).build();


    @Produces
    DataLayerObject produceDataLayerObject() {
        return  Mockito.mock(DataLayerObject.class);
    }

    @Test
    void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callbackData = new CallbackData(ENABLE_EXCERCISE);
        callbackData.setEntityId(0L);

        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getData()).thenReturn(objectMapper.writeValueAsString(callbackData));
        when(callbackQuery.getMessage()).thenReturn(message);

        List<BotApiMethod<?>> list = weld.select(EnableExerciseMessageHandler.class).get().handle(callbackQuery);

        assertEquals("Exercise is enabled successfully.", ((EditMessageText) list.get(0)).getText());
    }
}
