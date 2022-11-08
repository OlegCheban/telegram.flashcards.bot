package ru.flashcards.telegram.bot.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.utils.Help;

public class StartCommand extends BotCommand {
    private DataLayerObject dataLayer;

    public StartCommand(String commandIdentifier, String description, DataLayerObject dataLayerObject) {
        super(commandIdentifier, description);
        dataLayer = dataLayerObject;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        dataLayer.registerUser(chat.getId(), getUserName(user));
        Help.sendBotManual(user.getId());
    }

    public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
    }

}
