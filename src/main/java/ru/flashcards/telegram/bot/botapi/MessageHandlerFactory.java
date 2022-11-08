package ru.flashcards.telegram.bot.botapi;

import ru.flashcards.telegram.bot.botapi.handlers.create.CreateDefinitionMessageHandler;
import ru.flashcards.telegram.bot.botapi.handlers.create.CreateFlashcardsMessageHandler;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

import java.util.Collections;

public class MessageHandlerFactory {

    public InputMessageHandler getHandler(String messageText, DataLayerObject dataLayer){
        if (messageText.contains("//")){
            return new CreateFlashcardsMessageHandler(dataLayer);
        }
        if (messageText.contains("##")){
            return new CreateDefinitionMessageHandler(dataLayer);
        }

        return message -> Collections.emptyList();
    }
}
