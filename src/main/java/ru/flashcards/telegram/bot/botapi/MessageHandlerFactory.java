package ru.flashcards.telegram.bot.botapi;

import ru.flashcards.telegram.bot.botapi.handlers.create.CreateDefinitionMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.create.CreateFlashcardsMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.FlashcardDataHandler;

import java.util.Collections;

public class MessageHandlerFactory {
    private FlashcardDataHandler flashcardDataHandler = new FlashcardDataHandler();

    public InputMessageHandler getHandler(String messageText){
        if (messageText.contains("//")){
            return new CreateFlashcardsMessageHandler(flashcardDataHandler);
        }
        if (messageText.contains("##")){
            return new CreateDefinitionMessageHandler(flashcardDataHandler);
        }

        return message -> Collections.emptyList();
    }
}
