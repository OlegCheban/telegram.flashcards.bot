package ru.flashcards.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.flashcards.telegram.bot.FlashcardsBot;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.sheduler.ScheduledTasks;

public class BotApplication {
    public static void main(String[] args) {
        try {
            ScheduledTasks scheduledTasks = new ScheduledTasks();
            scheduledTasks.run();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            DataLayerObject dataLayerObject = new DataLayerObject();
            botsApi.registerBot(new FlashcardsBot(dataLayerObject));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
