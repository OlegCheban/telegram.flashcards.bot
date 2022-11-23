package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.create.CreateDefinitionMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.create.CreateFlashcardsMessageHandler;

import javax.inject.Inject;
import java.util.Collections;

public class CreateFlashcardMessageFactory implements MessageHandlerAbstractFactory<MessageHandler<Message>> {
    @Inject
    private CreateFlashcardsMessageHandler createFlashcardsMessageHandler;

    @Inject
    private CreateDefinitionMessageHandler createDefinitionMessageHandler;

    @Override
    public MessageHandler<Message> getHandler(Message message) {
        if (message.getText().contains("//")){
            return createFlashcardsMessageHandler;
        }
        if (message.getText().contains("##")){
            return createDefinitionMessageHandler;
        }

        return m -> Collections.emptyList();
    }
}
