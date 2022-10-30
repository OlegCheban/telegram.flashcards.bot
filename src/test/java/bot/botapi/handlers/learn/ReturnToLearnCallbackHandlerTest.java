package bot.botapi.handlers.learn;

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
import ru.flashcards.telegram.bot.botapi.handlers.learn.ReturnToLearnCallbackHandler;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.FlashcardDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.SpacedRepetitionNotificationDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReturnToLearnCallbackHandlerTest {
    @Mock
    private FlashcardDataHandler flashcardDataHandler;
    @Mock
    private SpacedRepetitionNotificationDataHandler spacedRepetitionNotificationDataHandler;
    @Mock
    private ExerciseDataHandler exerciseDataHandler;

    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private CallbackData callbackData;


    @Test
    void test() throws NoSuchFieldException, IllegalAccessException {
        UserFlashcard flashcard = new UserFlashcard( "description", "transcription", "translation", "word");

        when(message.getChatId()).thenReturn(0L);
        when(message.getMessageId()).thenReturn(0);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(flashcardDataHandler.findUserFlashcardById(0L)).thenReturn(flashcard);

        ReturnToLearnCallbackHandler handler = new ReturnToLearnCallbackHandler(callbackData);

        Field flashcardDataHandlerField = handler.getClass().getDeclaredField("flashcardDataHandler");
        flashcardDataHandlerField.setAccessible(true);
        flashcardDataHandlerField.set(handler, flashcardDataHandler);

        Field spacedRepetitionNotificationDataHandlerField = handler.getClass().getDeclaredField("spacedRepetitionNotificationDataHandler");
        spacedRepetitionNotificationDataHandlerField.setAccessible(true);
        spacedRepetitionNotificationDataHandlerField.set(handler, spacedRepetitionNotificationDataHandler);

        Field exerciseDataHandlerField = handler.getClass().getDeclaredField("exerciseDataHandler");
        exerciseDataHandlerField.setAccessible(true);
        exerciseDataHandlerField.set(handler, exerciseDataHandler);

        List<BotApiMethod<?>> list = Mockito.spy(handler).handle(callbackQuery);

        assertEquals("*Flashcard returned to learn*\n*word* \\[transcription]\n\ndescription\n\ntranslation", ((EditMessageText) list.get(0)).getText());
    }
}
