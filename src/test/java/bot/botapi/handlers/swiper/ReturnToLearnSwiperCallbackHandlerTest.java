package bot.botapi.handlers.swiper;

import bot.FlashcardsBotTestAbstract;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.SWIPER_RETURN_TO_LEARN;

public class ReturnToLearnSwiperCallbackHandlerTest extends FlashcardsBotTestAbstract {
    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private SwiperFlashcard swiperFlashcard;

    @Test
    @Override
    protected void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callbackData = new CallbackData(SWIPER_RETURN_TO_LEARN);
        callbackData.setEntityId(0L);

        when(swiperFlashcard.getWord()).thenReturn("word");
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getData()).thenReturn(objectMapper.writeValueAsString(callbackData));
        when(callbackQuery.getMessage()).thenReturn(message);
        when(dataLayer.getSwiperFlashcard(0L,0L,null)).thenReturn(swiperFlashcard);

        List<BotApiMethod<?>> list = (List<BotApiMethod<?>>) handleCallbackQueryInputMethod().invoke(testBot, callbackQuery);
        assertEquals("*word* returned to learn", ((SendMessage) list.get(1)).getText());

    }
}
