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

import ru.flashcards.telegram.bot.botapi.handlers.learn.ProceedToRepetitionCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import javax.enterprise.inject.Produces;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.BotKeyboardButton.PROCEED_LEARNING;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class ProceedToRepetitionCallbackHandlerTest {
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private Message message;

    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(ProceedToRepetitionCallbackHandler.class, ProceedToRepetitionCallbackHandlerTest.class).build();

    @Produces
    DataLayerObject produceDataLayerObject() {
        UserFlashcard userFlashcard = Mockito.mock(UserFlashcard.class);
        DataLayerObject dataLayerObject = Mockito.mock(DataLayerObject.class);

        when(userFlashcard.getWord()).thenReturn("word");
        when(userFlashcard.getDescription()).thenReturn("description");
        when(userFlashcard.getTranscription()).thenReturn("transcription");
        when(userFlashcard.getTranslation()).thenReturn("translation");

        when(dataLayerObject.findUserFlashcardById(0L)).thenReturn(userFlashcard);

        return  dataLayerObject;
    }

    @Test
    void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callbackData = new CallbackData(PROCEED_LEARNING);
        callbackData.setEntityId(0L);

        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getData()).thenReturn(objectMapper.writeValueAsString(callbackData));
        when(callbackQuery.getMessage()).thenReturn(message);

        List<BotApiMethod<?>> list = weld.select(ProceedToRepetitionCallbackHandler.class).get().handle(callbackQuery);

        assertEquals("*Spaced repetition*\n*word* /transcription/\n\ndescription\n\ntranslation", ((EditMessageText) list.get(0)).getText());
    }
}
