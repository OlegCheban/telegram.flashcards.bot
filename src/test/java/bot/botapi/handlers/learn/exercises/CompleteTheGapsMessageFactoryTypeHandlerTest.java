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
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.CompleteTheGapsMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.common.ExerciseProvider;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.utils.RandomMessageText;

import javax.enterprise.inject.Produces;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class CompleteTheGapsMessageFactoryTypeHandlerTest {
    @Mock
    private Message message;

    @WeldSetup
    private WeldInitiator weld = WeldInitiator.from(CompleteTheGapsMessageHandler.class, CompleteTheGapsMessageFactoryTypeHandlerTest.class).build();

    @Produces
    DataLayerObject dataLayer() {
        ExerciseFlashcard exerciseFlashcard = Mockito.mock(ExerciseFlashcard.class);
        DataLayerObject dataLayerObject = Mockito.mock(DataLayerObject.class);

        when(exerciseFlashcard.getWord()).thenReturn("wordValue");
        when(dataLayerObject.getCurrentExercise(0L)).thenReturn(exerciseFlashcard);

        return  dataLayerObject;
    }

    @Produces
    ExerciseProvider exerciseProvider() {
        ExerciseProvider exerciseProvider = Mockito.mock(ExerciseProvider.class);

        return exerciseProvider;
    }

    @Test
    void test(){
        when(message.getChatId()).thenReturn(0L);
        when(message.getText()).thenReturn("wordValue");
        List<BotApiMethod<?>> list = weld.select(CompleteTheGapsMessageHandler.class).get().handle(message);

        assertTrue(RandomMessageText.positiveMessages.contains(((SendMessage) list.get(0)).getText()));
    }
}
