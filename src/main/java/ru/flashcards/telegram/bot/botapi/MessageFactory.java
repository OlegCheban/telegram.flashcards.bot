package ru.flashcards.telegram.bot.botapi;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.flashcards.telegram.bot.botapi.handlers.other.OthersMessagesHandler;
import ru.flashcards.telegram.bot.botapi.handlers.translation.ChangeTranslationMessageHandler;

import javax.inject.Inject;
import java.util.Collections;


public class MessageFactory implements MessageHandlerAbstractFactory<MessageHandler<Message>> {
    @Inject
    private ChangeTranslationMessageHandler changeTranslationMessageHandler;

    @Inject
    private OthersMessagesHandler othersMessagesHandler;

    @Override
    public MessageHandler<Message> getHandler(Message message) {
        var messageType = UserFlashcardModificationBuffer.getMessageType(message.getChatId());

        switch (messageType){
            case CHANGE_TRANSLATION:
                return changeTranslationMessageHandler;
            case OTHER:
                return othersMessagesHandler;
        }

        return m -> Collections.emptyList();
    }
}
