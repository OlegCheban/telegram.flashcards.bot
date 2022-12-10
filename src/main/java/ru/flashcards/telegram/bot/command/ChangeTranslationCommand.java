package ru.flashcards.telegram.bot.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;
import ru.flashcards.telegram.bot.service.SendService;
import ru.flashcards.telegram.bot.botapi.UserFlashcardModificationBuffer;

import static ru.flashcards.telegram.bot.botapi.Literals.UNRECOGNIZED_OPTION_MSG;
import static ru.flashcards.telegram.bot.botapi.MessageType.CHANGE_TRANSLATION;

public class ChangeTranslationCommand extends BotCommand {
    private final String badParameters = UNRECOGNIZED_OPTION_MSG;
    private DataLayerObject dataLayer;

    public ChangeTranslationCommand(String commandIdentifier, String description, DataLayerObject dataLayerObject) {
        super(commandIdentifier, description);
        this.dataLayer = dataLayerObject;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        super.processMessage(absSender, message, arguments);
        String text = "";

        if (arguments.length > 1) {
            SendService.sendMessage(message.getChatId(), badParameters);
            return;
        }
        if (arguments.length == 1){
            text = arguments[0];
        }

        UserFlashcard userFlashcard = dataLayer.findUserFlashcardByName(message.getChatId(), text.trim());
        if (userFlashcard == null){
            SendService.sendMessage(message.getChatId(), "Flashcard wasn't put to your profile.");
        } else {
            SendService.sendMessage(message.getChatId(), "OK. Send me a new translation.");
            UserFlashcardModificationBuffer.putRequest(message.getChatId(), userFlashcard.getId(), CHANGE_TRANSLATION);
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

    }
}
