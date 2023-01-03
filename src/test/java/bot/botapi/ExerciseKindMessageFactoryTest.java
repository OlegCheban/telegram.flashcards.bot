package bot.botapi;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.*;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.*;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static ru.flashcards.telegram.bot.botapi.ExerciseKind.MEMORISED;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class ExerciseKindMessageFactoryTest {
    @WeldSetup
    private WeldInitiator weld =
            WeldInitiator
                    .from(ExerciseMessageFactory.class, ExerciseKindMessageFactoryTest.class)
                    .activate(RequestScoped.class)
                    .build();

    @Mock
    private Message message;

    @Produces
    MemorisedMessageHandler produceMemorisedMessageHandler(){
        return Mockito.mock(MemorisedMessageHandler.class);
    }
    @Produces
    StopLearningMessageHandler produceStopLearningMessageHandler(){
        return Mockito.mock(StopLearningMessageHandler.class);
    }
    @Produces
    CheckDescriptionMessageHandler produceCheckDescriptionMessageHandler(){
        return Mockito.mock(CheckDescriptionMessageHandler.class);
    }
    @Produces
    CheckTranslationMessageHandler produceCheckTranslationMessageHandler(){
        return Mockito.mock(CheckTranslationMessageHandler.class);
    }
    @Produces
    CheckSpellingMessageHandler produceCheckSpellingMessageHandler(){
        return Mockito.mock(CheckSpellingMessageHandler.class);
    }
    @Produces
    CompleteTheGapsMessageHandler produceCompleteTheGapsMessageHandler(){
        return Mockito.mock(CompleteTheGapsMessageHandler.class);
    }

    @Produces
    DataLayerObject produceDataLayerObject(){
        DataLayerObject dataLayerObject = Mockito.mock(DataLayerObject.class);
        ExerciseFlashcard exerciseFlashcard = Mockito.mock(ExerciseFlashcard.class);
        when(exerciseFlashcard.getExerciseKindCode()).thenReturn(MEMORISED);
        when(dataLayerObject.getCurrentExercise(0L)).thenReturn(exerciseFlashcard);
        return dataLayerObject;
    }

    @Test
    void test(){
        when(message.getText()).thenReturn("");
        MessageHandler<Message> handler = weld.select(ExerciseMessageFactory.class).get().getHandler(message);
        assertThat(handler, instanceOf(MemorisedMessageHandler.class));
    }
}
