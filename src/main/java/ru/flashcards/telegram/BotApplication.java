package ru.flashcards.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.flashcards.telegram.bot.FlashcardsBot;
import ru.flashcards.telegram.bot.exception.TelegramApiRuntimeException;
import ru.flashcards.telegram.bot.sheduler.ScheduledTasks;

public class BotApplication {
    public static void main(String[] args) {
        try {
            ScheduledTasks scheduledTasks = new ScheduledTasks();
            scheduledTasks.run();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new FlashcardsBot());
        } catch (TelegramApiException e) {
            throw new TelegramApiRuntimeException(e);
        }
    }
}
