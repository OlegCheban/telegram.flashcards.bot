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
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.botapi.handlers.learn.AddToLearnAndNextCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.Flashcard;

import javax.enterprise.inject.Produces;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.ADD_TO_LEARN_AND_NEXT;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class AddToLearnAndNextCallbackHandlerTest {
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private Message message;

    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(AddToLearnAndNextCallbackHandler.class, AddToLearnAndNextCallbackHandlerTest.class).build();


    @Produces
    DataLayerObject produceDataLayerObject() {
        Flashcard flashcard = Mockito.mock(Flashcard.class);
        DataLayerObject dataLayerObject = Mockito.mock(DataLayerObject.class);

        when(flashcard.getWord()).thenReturn("word");
        when(dataLayerObject.findFlashcardById(0L)).thenReturn(flashcard);

        return  dataLayerObject;
    }

    @Test
    void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callbackData = new CallbackData(ADD_TO_LEARN_AND_NEXT);
        callbackData.setEntityId(0L);

        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getData()).thenReturn(objectMapper.writeValueAsString(callbackData));
        when(callbackQuery.getMessage()).thenReturn(message);

        List<BotApiMethod<?>> list = weld.select(AddToLearnAndNextCallbackHandler.class).get().handle(callbackQuery);

        assertEquals("Flashcard *word* added for learning", ((EditMessageText) list.get(0)).getText());
    }
}
