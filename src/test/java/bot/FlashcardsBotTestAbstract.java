package bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.FlashcardsBot;

import java.lang.reflect.Method;

@Deprecated
@ExtendWith(MockitoExtension.class)
public abstract class FlashcardsBotTestAbstract {
    @Mock
    protected Message message;
    protected FlashcardsBot testBot;

    @BeforeEach
    void setUp() {
        testBot = Mockito.spy(new FlashcardsBot());
    }

    protected Method handleMessageInputMethod() throws NoSuchMethodException {
        Method handleMessageInputMethod = FlashcardsBot.class.getDeclaredMethod("handleMessageInput", Message.class);
        handleMessageInputMethod.setAccessible(true);
        return handleMessageInputMethod;
    }

    protected Method handleCallbackQueryInputMethod() throws NoSuchMethodException {
        Method handleCallbackQueryInputMethod = FlashcardsBot.class.getDeclaredMethod("handleCallbackQueryInput", CallbackQuery.class);
        handleCallbackQueryInputMethod.setAccessible(true);
        return handleCallbackQueryInputMethod;
    }

    protected abstract void test() throws Exception;

}
