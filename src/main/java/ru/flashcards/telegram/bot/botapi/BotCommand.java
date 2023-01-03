package ru.flashcards.telegram.bot.botapi;

public enum BotCommand {
    START("start"),
    START_LEARNING("l"),
    START_WATERING_SESSION("w"),
    ENABLE_EXCERCISE("e"),
    DISABLE_EXCERCISE("d"),
    OPEN_SWIPER("s"),
    FIND_FLASHCARD("f"),
    NOTIFICATION_INTERVAL_SETTINGS("ni"),
    TRAINING_FLASHCARDS_QUANTITY_SETTINGS("fq"),
    WATERING_SESSION_REPLY_TIME_SETTINGS("wt"),
    CHANGE_TRANSLATION("ed"),
    HELP("h"),
    STOP_LEARNING("Stop learning");

    public final String command;

    BotCommand(String command) {
        this.command = command;
    }
}
