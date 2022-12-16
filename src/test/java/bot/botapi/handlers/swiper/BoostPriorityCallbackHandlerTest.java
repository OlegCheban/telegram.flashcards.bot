package bot.botapi.handlers.swiper;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;
import ru.flashcards.telegram.bot.botapi.handlers.swiper.BoostPriorityCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;

import javax.enterprise.inject.Produces;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.Literals.BOOST_PRIORITY;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class BoostPriorityCallbackHandlerTest {
    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;
    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(BoostPriorityCallbackHandler.class, BoostPriorityCallbackHandlerTest.class).build();
    @Produces
    DataLayerObject produceDataLayerObject() {
        SwiperFlashcard swiperFlashcard = Mockito.mock(SwiperFlashcard.class);
        DataLayerObject dataLayerObject = Mockito.mock(DataLayerObject.class);

        when(swiperFlashcard.getWord()).thenReturn("word");
        when(dataLayerObject.getSwiperFlashcard(0L,0L,null, null)).thenReturn(swiperFlashcard);

        return  dataLayerObject;
    }

    @Test
    void test() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData callbackData = new CallbackData(BOOST_PRIORITY);
        callbackData.setEntityId(0L);

        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getData()).thenReturn(objectMapper.writeValueAsString(callbackData));
        when(callbackQuery.getMessage()).thenReturn(message);

        List<BotApiMethod<?>> list = weld.select(BoostPriorityCallbackHandler.class).get().handle(callbackQuery);
        assertEquals("*word* added to next learning session", ((SendMessage) list.get(1)).getText());
    }
}
