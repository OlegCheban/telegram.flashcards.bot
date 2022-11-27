package ru.flashcards.telegram.bot.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.flashcards.telegram.bot.botapi.wateringSession.WateringSession;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

public class StartWateringSessionCommand extends BotCommand {
    private DataLayerObject dataLayer;

    public StartWateringSessionCommand(String commandIdentifier, String description, DataLayerObject dataLayer) {
        super(commandIdentifier, description);
        this.dataLayer = dataLayer;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            if (dataLayer.existsLearnedFlashcards(user.getId())){
                dataLayer.setWateringSessionMode(user.getId(), true);
                WateringSession wateringSession = new WateringSession(dataLayer);
                absSender.execute(wateringSession.newFlashcard(chat.getId()));
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("There aren't any learned flashcards in your profile.");
                sendMessage.setChatId(String.valueOf(chat.getId()));
                absSender.execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
