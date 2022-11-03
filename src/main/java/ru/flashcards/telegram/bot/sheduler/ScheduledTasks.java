package ru.flashcards.telegram.bot.sheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.RandomNotificationDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.SpacedRepetitionNotificationDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcardPushMono;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcardSpacedRepetitionNotification;
import ru.flashcards.telegram.bot.service.SendService;
import ru.flashcards.telegram.bot.utils.Lambda;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.TimeUnit.*;
import static ru.flashcards.telegram.bot.botapi.Literals.*;

public class ScheduledTasks {
    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private final String pushpinEmoji = "\uD83D\uDCCC";
    private final String alarmEmoji = "\u23F0";
    private SpacedRepetitionNotificationDataHandler spacedRepetitionNotificationDataHandler;
    private RandomNotificationDataHandler randomNotificationDataHandler;
    private ExerciseDataHandler exerciseDataHandler;

    public ScheduledTasks() {
        randomNotificationDataHandler = new RandomNotificationDataHandler();
        spacedRepetitionNotificationDataHandler = new SpacedRepetitionNotificationDataHandler();
        exerciseDataHandler = new ExerciseDataHandler();
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

        Runnable exerciseListener = () -> {
            try {
                runExercises();
            } catch ( Exception e ) {
                logger.error( "Caught exception in ScheduledExecutorService. StackTrace:\n" + e.getStackTrace());
            }
        };

        scheduler.scheduleAtFixedRate(exerciseListener, 1, 1, SECONDS);

        //Runnable canceller = () -> handle.cancel(false);
        //scheduler.schedule(canceller, 1, HOURS);
    }

    private void runSpacedRepetitionNotification(){
        if (!spacedRepetitionNotificationDataHandler.isPushQueueUpToCurrentDate()){
            spacedRepetitionNotificationDataHandler.refreshIntervalNotification();
        }
        List<UserFlashcardSpacedRepetitionNotification> userFlashcardSpacedRepetitionNotifications =
                spacedRepetitionNotificationDataHandler.getUserFlashcardsSpacedRepetitionNotification();

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
                spacedRepetitionNotificationDataHandler.addFlashcardPushHistory(queue.getUserFlashcardId());
            }
        });
    }

    private void runRandomNotification(){
        List<UserFlashcardPushMono> userFlashcardPushMonos = randomNotificationDataHandler.getUserFlashcardsRandomNotification();

        userFlashcardPushMonos.forEach((queue) -> {
            List<JSONObject> listButtons = new ArrayList<>();
            listButtons.add(prepareButton(queue.getUserFlashcardId(), "show translation", TRANSLATE));
            listButtons.add(prepareButton(queue.getUserFlashcardId(), "example of usage", EXAMPLES));

            if (queue.getLastPushTimestamp() == null || queue.getLastPushTimestamp().plusMinutes(queue.getNotificationInterval()).isBefore(LocalDateTime.now())) {
                SendService.sendMessage(queue.getUserId(), "*"+queue.getWord()+"* \\[" + queue.getTranscription() + "] " + pushpinEmoji + "\n\n"+queue.getDescription(),
                        String.valueOf(createButtonMenu(listButtons)));

                randomNotificationDataHandler.updatePushTimestampById(queue.getUserFlashcardId());
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

    private void runExercises(){
        List<ExerciseFlashcard> sendToLearnFlashcards = exerciseDataHandler.getExercise();

        sendToLearnFlashcards.forEach((row) -> {
            List<String> wrongAnswers = null;
            if (row.getExerciseCode().equals(MEMORISED)) {
                SendService.sendMessage(row.getChatId(),
                        "*" + row.getWord() + "* \\[" + row.getTranscription() + "]\n" + row.getDescription() + "\n\n*Translation:* " + row.getTranslation(),
                        String.valueOf(memorisedKeyboardJson()));

            } else if (row.getExerciseCode().equals(CHECK_DESCRIPTION)){
                wrongAnswers = exerciseDataHandler.getRandomDescriptions();

                SendService.sendMessage(row.getChatId(),
                        "Choose correct description for flashcard *" + row.getWord() + "* \\[" + row.getTranscription() + "]\n\n",
                        String.valueOf(answersInlineKeyboardJson(wrongAnswers, row.getDescription())));

            } else if (row.getExerciseCode().equals(CHECK_TRANSLATION)){
                wrongAnswers = exerciseDataHandler.getRandomTranslations();

                SendService.sendMessage(row.getChatId(),
                        "Choose correct translation for flashcard *" + row.getWord() + "* \\[" + row.getTranscription() + "]\n\n",
                        String.valueOf(answersInlineKeyboardJson(wrongAnswers, row.getTranslation())));

            } else if (row.getExerciseCode().equals(CHECK_SPELLING) || row.getExerciseCode().equals(CHECK_SPELLING_WITH_HELPS)){
                JSONArray inlineKeyboardArrays = new JSONArray();
                JSONObject replyMarkupObj = new JSONObject();

                inlineKeyboardArrays.put(createButton(STOP_LEARNING));
                replyMarkupObj.put("keyboard", inlineKeyboardArrays);
                replyMarkupObj.put("resize_keyboard", true);

                String messageText = String.format("Write the correct spelling for flashcard *%s*", row.getTranslation());
                if (row.getExerciseCode().equals(CHECK_SPELLING_WITH_HELPS)) {
                    messageText = messageText + "\n\nHint: " + hideLetters(row.getWord()).replaceAll ("\\*","\\\\*");
                }
                SendService.sendMessage(row.getChatId(), messageText, String.valueOf(replyMarkupObj));

            } else if (row.getExerciseCode().equals(COMPLETE_THE_GAPS)){
                wrongAnswers = exerciseDataHandler.getRandomWords();

                SendService.sendMessage(row.getChatId(),
                        "Complete the gaps in sentence below. \n\n" + row.getExample().replaceAll("\\*([a-zA-Z]+)\\*", "\\\\_\\\\_\\\\_\\\\_"),
                        String.valueOf(answersInlineKeyboardJson(wrongAnswers, row.getWord())));
            }

            exerciseDataHandler.setLock(row.getChatId(), true);
        });
    }

    private String hideLetters(String word){
        StringBuilder result = new StringBuilder(word);
        double hidePrc = 80;
        int hiddenLettersQty = (int) (word.length()/100d*hidePrc);
        new Random().ints(1, word.length()).distinct().limit(hiddenLettersQty).forEach(
                i -> result.setCharAt(i, '*' )
        );

        return String.valueOf(result);
    }

    private JSONObject memorisedKeyboardJson(){
        JSONArray inlineKeyboardArrays = new JSONArray();
        inlineKeyboardArrays.put(createButton(MEMORISED));
        inlineKeyboardArrays.put(createButton(STOP_LEARNING));
        JSONObject mainObj = new JSONObject();
        mainObj.put("keyboard", inlineKeyboardArrays);
        mainObj.put("resize_keyboard", true);
        return mainObj;
    }

    private JSONObject answersInlineKeyboardJson(List<String> wrongAnswersList, String correctAnswer){
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        final int lastButton = 3;
        JSONArray inlineKeyboardArrays = new JSONArray();

        wrongAnswersList.forEach(Lambda.forEachWithCounter( (i, descr) -> {
            if (i == randomNum){
                inlineKeyboardArrays.put(createButton(correctAnswer));
            }
            inlineKeyboardArrays.put(createButton(descr));

            if (i == wrongAnswersList.size() && randomNum == lastButton){
                inlineKeyboardArrays.put(createButton(correctAnswer));
            }
        }));
        inlineKeyboardArrays.put(createButton(STOP_LEARNING));

        JSONObject mainObj = new JSONObject();
        mainObj.put("keyboard", inlineKeyboardArrays);
        mainObj.put("resize_keyboard", true);

        return mainObj;
    }

    private JSONArray createButton(String text){
        JSONArray inlineKeyboardButtonArrayJson = new JSONArray();
        JSONObject trueDescrInlineKeyboardButtonJson = new JSONObject();
        trueDescrInlineKeyboardButtonJson.put("text", text);
        inlineKeyboardButtonArrayJson.put(trueDescrInlineKeyboardButtonJson);

        return inlineKeyboardButtonArrayJson;
    }
}
