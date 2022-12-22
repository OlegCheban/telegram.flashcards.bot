package bot.botapi;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.flashcards.telegram.bot.botapi.CallbackFactory;
import ru.flashcards.telegram.bot.botapi.CallbackFactoryProvider;
import ru.flashcards.telegram.bot.botapi.CallbackHandlerAbstractFactory;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import static ru.flashcards.telegram.bot.botapi.MessageFactoryType.CALLBACK;


@ExtendWith(WeldJunit5Extension.class)
@ExtendWith(MockitoExtension.class)
public class CallbackFactoryProviderTest {
    @WeldSetup
    private WeldInitiator weld =
            WeldInitiator
                    .from(CallbackFactoryProvider.class, CallbackFactoryProviderTest.class)
                    .activate(RequestScoped.class)
                    .build();

    @Produces
    CallbackFactory produceCallbackFactory(){
        CallbackFactory callbackFactory = Mockito.mock(CallbackFactory.class);
        return callbackFactory;
    }

    @Test
    void test(){
        CallbackHandlerAbstractFactory abstractFactory = weld.select(CallbackFactoryProvider.class).get().getFactory(CALLBACK);
        assertThat(abstractFactory, instanceOf(CallbackFactory.class));
    }
}
