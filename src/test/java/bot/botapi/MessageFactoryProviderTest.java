package bot.botapi;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.flashcards.telegram.bot.botapi.*;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.flashcards.telegram.bot.botapi.MessageFactoryType.*;

@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class MessageFactoryProviderTest {
    @WeldSetup
    private WeldInitiator weld =
            WeldInitiator
                    .from(MessageFactoryProvider.class, MessageFactoryProviderTest.class)
                    .activate(RequestScoped.class)
                    .build();

    @Produces
    ExerciseMessageFactory produceExerciseMessageFactory(){
        return Mockito.mock(ExerciseMessageFactory.class);
    }

    @Produces
    WateringSessionMessageFactory produceWateringSessionMessageFactory(){
        return Mockito.mock(WateringSessionMessageFactory.class);
    }

    @Produces
    MessageFactory produceCallbackFactory(){
        return Mockito.mock(MessageFactory.class);
    }

    @Test
    void test(){
        MessageHandlerAbstractFactory abstractFactory = weld.select(MessageFactoryProvider.class).get().getFactory(EXERCISE);
        assertThat(abstractFactory, instanceOf(ExerciseMessageFactory.class));

        abstractFactory = weld.select(MessageFactoryProvider.class).get().getFactory(WATERING_SESSION);
        assertThat(abstractFactory, instanceOf(WateringSessionMessageFactory.class));

        abstractFactory = weld.select(MessageFactoryProvider.class).get().getFactory(OTHER_MESSAGES);
        assertThat(abstractFactory, instanceOf(MessageFactory.class));
    }

}
