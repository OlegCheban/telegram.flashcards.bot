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

import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.PROCEED_LEARNING;

public class ProceedToRepetitionCallbackHandlerTest extends FlashcardsBotTestAbstract {
    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private UserFlashcard userFlashcard;

    @Test
    @Override
    protected void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callbackData = new CallbackData(PROCEED_LEARNING);
        callbackData.setEntityId(0L);

        when(userFlashcard.getWord()).thenReturn("word");
        when(userFlashcard.getDescription()).thenReturn("description");
        when(userFlashcard.getTranscription()).thenReturn("transcription");
        when(userFlashcard.getTranslation()).thenReturn("translation");
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getData()).thenReturn(objectMapper.writeValueAsString(callbackData));
        when(callbackQuery.getMessage()).thenReturn(message);
        when(dataLayer.findUserFlashcardById(0L)).thenReturn(userFlashcard);

        List<BotApiMethod<?>> list = (List<BotApiMethod<?>>) handleCallbackQueryInputMethod().invoke(testBot, callbackQuery);

        assertEquals("*Spaced repetition*\n*word* \\[transcription]\n\ndescription\n\ntranslation", ((EditMessageText) list.get(0)).getText());
    }
}
