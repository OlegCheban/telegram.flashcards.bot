package ru.flashcards.telegram.bot.command.modifyFlashcard;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.flashcards.telegram.bot.db.dmlOps.FlashcardDataHandler;
import ru.flashcards.telegram.bot.service.SendService;

public class ChangeTranslationCommand extends BotCommand {
    private final String badParameters = "Bad parameters";
    private FlashcardDataHandler flashcardDataHandler;

    public ChangeTranslationCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
        this.flashcardDataHandler = new FlashcardDataHandler();
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

        flashcardDataHandler.editTranslation(message.getChatId(), text.split("#")[0], text.split("#")[1]);
        SendService.sendMessage(message.getChatId(), "Done");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

    }
}
