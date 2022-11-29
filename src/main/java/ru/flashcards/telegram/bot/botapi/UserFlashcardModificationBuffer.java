package ru.flashcards.telegram.bot.botapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class UserFlashcardModificationBuffer extends HashMap<Long, HashMap<Long, MessageType>> {
    private static Logger logger = LoggerFactory.getLogger(UserFlashcardModificationBuffer.class);
    private static final UserFlashcardModificationBuffer instance;

    static {
        logger.info("UserFlashcardModificationBuffer static block initialization");
        instance = new UserFlashcardModificationBuffer();
    }

    private UserFlashcardModificationBuffer(){
    }

    public static void putRequest(Long chatId, Long userFlashcardId, MessageType messageType){
        HashMap<Long, MessageType> map = new HashMap<>();
        map.put(userFlashcardId, messageType);
        instance.put(chatId, map);
    }

    public static Long getUserFlashcardId(Long chatId){
        var map = instance.get(chatId);
        if (map != null){
            return (Long) map.keySet().toArray()[0];
        }

        return 0L;
    }

    public static MessageType getMessageType(Long chatId){
        var map = instance.get(chatId);
        if (map != null){
            return map.get(map.keySet().toArray()[0]);
        }

        return MessageType.OTHER;
    }

    public static void removeRequest(Long chatId){
        instance.remove(chatId);
    }
}
