package ru.flashcards.telegram.bot.command.addToLearn;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.flashcards.telegram.bot.service.SendService;

public class FindFlashcardCommand extends BotCommand {
    public FindFlashcardCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        super.processMessage(absSender, message, arguments);
        Long chatId = message.getChatId();

        if (arguments.length > 1) {
            SendService.sendMessage(message.getChatId(), "Unrecognize parameter");
            return;
        }
        SuggetFlashcard suggetFlashcard = new SuggetFlashcard();
        if (arguments.length == 1) {
            suggetFlashcard.byParam(chatId, arguments[0]);
        } else {
            suggetFlashcard.byTop3000Category(chatId);
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
    }

}
