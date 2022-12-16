package ru.flashcards.telegram.bot.sheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flashcards.telegram.bot.botapi.pojo.CallbackData;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcardPushMono;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcardSpacedRepetitionNotification;
import ru.flashcards.telegram.bot.service.SendService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;
import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class ScheduledTasks {
    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final String pushpinEmoji = "\uD83D\uDCCC";
    private final String alarmEmoji = "\u23F0";
    private DataLayerObject dataLayerObject;

    public ScheduledTasks() {
        dataLayerObject = new DataLayerObject();
    }

    public void run () {
        Runnable randomNotification = () -> {
            try {
                runRandomNotification();
            } catch ( Exception e ) {
                logger.error( "Caught exception in randomNotification thread. StackTrace:\n" + e.getStackTrace());
            }
        };

        scheduler.scheduleAtFixedRate(randomNotification, 1, 60, SECONDS);

        Runnable spacedRepetitionNotification = () -> {
            try {
                runSpacedRepetitionNotification();
            } catch ( Exception e ) {
                logger.error( "Caught exception in spacedRepetitionNotification thread. StackTrace:\n" + e.getStackTrace());
            }
        };

        scheduler.scheduleAtFixedRate(spacedRepetitionNotification, 1, 60, SECONDS);

        //Runnable canceller = () -> handle.cancel(false);
        //scheduler.schedule(canceller, 1, HOURS);
    }

    private void runSpacedRepetitionNotification(){
        if (!dataLayerObject.isPushQueueUpToCurrentDate()){
            dataLayerObject.refreshIntervalNotification();
        }
        List<UserFlashcardSpacedRepetitionNotification> userFlashcardSpacedRepetitionNotifications =
                dataLayerObject.getUserFlashcardsSpacedRepetitionNotification();

        userFlashcardSpacedRepetitionNotifications.forEach((queue) -> {
            List<JSONObject> listButtons = new ArrayList<>();
            listButtons.add(prepareButton(queue.getUserFlashcardId(), "Yes", PROCEED_LEARNING));
            listButtons.add(prepareButton(queue.getUserFlashcardId(), "No", RETURN_TO_LEARN));


            if (queue.getNotificationDate().isBefore(LocalDateTime.now())){
                SendService.sendMessage(queue.getUserId(),
                        "*Spaced repetition* " + alarmEmoji +
                                "\n*" + queue.getWord()+ "* \\[" + queue.getTranscription() + "] ("+queue.getPrc()+"% learned)" +
                                "\n\n Do you remember this word?",
                        String.valueOf(createButtonMenu(listButtons)));
                dataLayerObject.addFlashcardPushHistory(queue.getUserFlashcardId());
            }
        });
    }

    private void runRandomNotification(){
        List<UserFlashcardPushMono> userFlashcardPushMonos = dataLayerObject.getUserFlashcardsRandomNotification();

        userFlashcardPushMonos.forEach((queue) -> {
            List<JSONObject> listButtons = new ArrayList<>();
            listButtons.add(prepareButton(queue.getUserFlashcardId(), "translate", TRANSLATE));
            listButtons.add(prepareButton(queue.getUserFlashcardId(), "example of usage", EXAMPLES));

            if (queue.getLastPushTimestamp() == null || queue.getLastPushTimestamp().plusMinutes(queue.getNotificationInterval()).isBefore(LocalDateTime.now())) {
                SendService.sendMessage(queue.getUserId(), "*"+queue.getWord()+"* \\[" + queue.getTranscription() + "] " + pushpinEmoji + "\n\n"+queue.getDescription(),
                        String.valueOf(createButtonMenu(listButtons)));

                dataLayerObject.updatePushTimestampById(queue.getUserFlashcardId());
            }
        });
    }

    private JSONObject prepareButton(Long userFlashcardId, String label, String command){
        CallbackData callbackData = new CallbackData(command);
        callbackData.setEntityId(userFlashcardId);

        JSONObject inlineKeyboardButtonJson = new JSONObject();
        inlineKeyboardButtonJson.put("text", label);

        try {
            inlineKeyboardButtonJson.put("callback_data", objectMapper.writeValueAsString(callbackData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return inlineKeyboardButtonJson;
    }

    private JSONObject createButtonMenu(List<JSONObject> list){
        JSONObject mainObj = new JSONObject();
        JSONArray inlineKeyboardButtonArrayJson = new JSONArray();
        list.forEach(button -> inlineKeyboardButtonArrayJson.put(button));
        JSONArray inlineKeyboardArrays = new JSONArray();
        inlineKeyboardArrays.put(inlineKeyboardButtonArrayJson);
        mainObj.put("inline_keyboard", inlineKeyboardArrays);

        return mainObj;
    }
}
