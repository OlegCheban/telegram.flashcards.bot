package ru.flashcards.telegram.bot.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.service.SendService;
import ru.flashcards.telegram.bot.utils.Number;

public class TrainingFlashcardsQuantitySettingsCommand extends BotCommand {
    private DataLayerObject dataLayer;

    public TrainingFlashcardsQuantitySettingsCommand(String commandIdentifier, String description, DataLayerObject dataLayerObject) {
        super(commandIdentifier, description);
        this.dataLayer = dataLayerObject;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        super.processMessage(absSender, message, arguments);
        Long chatId = message.getChatId();

        if (Number.isInteger(arguments[0], 10)) {
            SendService.sendMessage(message.getChatId(), "Done");
            dataLayer.setTrainingFlashcardsQuantity(Integer.valueOf(arguments[0]), chatId);
        } else {
            SendService.sendMessage(message.getChatId(), "Wrong parameter, should be a number");
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
    }
}
