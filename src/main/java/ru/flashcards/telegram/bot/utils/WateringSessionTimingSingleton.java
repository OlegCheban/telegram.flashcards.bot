package ru.flashcards.telegram.bot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flashcards.telegram.bot.sheduler.ScheduledTasks;

import java.time.LocalDateTime;
import java.util.HashMap;

public class WateringSessionTimingSingleton extends HashMap<Long, LocalDateTime> {
    private static Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final WateringSessionTimingSingleton instance;

    static {
        logger.info("WateringSessionTimingSingleton static block initialization");
        instance = new WateringSessionTimingSingleton();
    }

    private WateringSessionTimingSingleton(){
    }

    public static LocalDateTime setStartDateTime(Long chatId, LocalDateTime localDateTime){
        return instance.put(chatId, localDateTime);
    }

    public static LocalDateTime getStartDateTime(Long chatId){
        return instance.get(chatId);
    }
}
