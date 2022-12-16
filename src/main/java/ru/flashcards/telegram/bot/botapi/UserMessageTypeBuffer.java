package ru.flashcards.telegram.bot.botapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class UserMessageTypeBuffer extends HashMap<Long, HashMap<Long, MessageType>> {
    private static Logger logger = LoggerFactory.getLogger(UserMessageTypeBuffer.class);
    private static final UserMessageTypeBuffer instance;

    static {
        logger.info("UserFlashcardModificationBuffer static block initialization");
        instance = new UserMessageTypeBuffer();
    }

    private UserMessageTypeBuffer(){
    }

    public static void putRequest(Long chatId, Long entityId, MessageType messageType){
        HashMap<Long, MessageType> map = new HashMap<>();
        map.put(entityId, messageType);
        instance.put(chatId, map);
    }

    public static Long getEntityId(Long chatId){
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
