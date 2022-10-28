package ru.flashcards.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.flashcards.telegram.bot.Bot;
import ru.flashcards.telegram.bot.sheduler.ScheduledTasks;

public class BotApplication {
    private static final String BOT_NAME = System.getenv().get("FLASHCARDS_BOT_NAME");
    private static final String BOT_TOKEN = System.getenv().get("FLASHCARDS_BOT_TOKEN");
    public static void main(String[] args) {
        try {
            ScheduledTasks scheduledTasks = new ScheduledTasks();
            scheduledTasks.run();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(BOT_NAME, BOT_TOKEN));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
