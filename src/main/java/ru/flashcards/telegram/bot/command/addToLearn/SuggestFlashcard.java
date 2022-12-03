package ru.flashcards.telegram.bot.command.addToLearn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SendToLearnFlashcard;
import ru.flashcards.telegram.bot.service.SendService;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static ru.flashcards.telegram.bot.botapi.Literals.*;
import static ru.flashcards.telegram.bot.botapi.Literals.EXCLUDE;

public class SuggestFlashcard {
    private DataLayerObject dataLayer;

    @Inject
    public SuggestFlashcard(DataLayerObject dataLayer) {
        this.dataLayer = dataLayer;
    }

    public void byParam(Long chatId, String param){
        List<SendToLearnFlashcard> sendToLearnFlashcards = dataLayer.getFlashcardsByWordToSuggestLearning(chatId, param);
        sendToLearnFlashcards.forEach((queue) -> {
            try {
                SendService.sendMessage(queue.getUserId(),
                        "*" + queue.getWord() + "* \\[" + queue.getTranscription() + "]\n" + queue.getDescription() + "\n\n*Translation:* " + queue.getTranslation() + "\n" +
                                dataLayer.getExamplesByFlashcardId(queue.getFlashcardId()).stream().map(Objects::toString).collect(Collectors.joining("\n", "*Examples:*\n", "")),
                        String.valueOf(prepareLearnButtonsInlineKeyboardJson(queue.getFlashcardId(), ADD_TO_LEARN, EXCLUDE))
                );

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        if (sendToLearnFlashcards.isEmpty()){
            SendService.sendMessage(chatId, "Flashcard *" +param + "* hasn't been found");
        }
    }

    public void byTop3000Category(Long chatId){
        List<SendToLearnFlashcard> sendToLearnFlashcards = dataLayer.getFlashcardsByCategoryToSuggestLearning(chatId, 713L);
        sendToLearnFlashcards.forEach((queue) -> {
            try {
                SendService.sendMessage(queue.getUserId(),
                        "*" + queue.getWord() + "* \\[" + queue.getTranscription() + "]\n" + queue.getDescription() + "\n\n*Translation:* " + queue.getTranslation() + "\n" +
                                dataLayer.getExamplesByFlashcardId(queue.getFlashcardId()).stream().map(Objects::toString).collect(Collectors.joining("\n","*Examples:*\n", "")),
                        String.valueOf(prepareLearnButtonsInlineKeyboardJson(queue.getFlashcardId(), ADD_TO_LEARN_AND_NEXT, EXCLUDE_AND_NEXT))
                );

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    private JSONObject prepareLearnButtonsInlineKeyboardJson(Long flashcardId, String addToLearnCommand, String excludeCommand) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CallbackData addToLearn = new CallbackData(addToLearnCommand);
        addToLearn.setEntityId(flashcardId);
        CallbackData exclude = new CallbackData(excludeCommand);
        exclude.setEntityId(flashcardId);
        CallbackData quite = new CallbackData(QUITE);
        exclude.setEntityId(flashcardId);

        JSONObject addToLearnInlineKeyboardButtonJson = new JSONObject();
        addToLearnInlineKeyboardButtonJson.put("text","add to learn");
        addToLearnInlineKeyboardButtonJson.put("callback_data", objectMapper.writeValueAsString(addToLearn));

        JSONObject excludeInlineKeyboardButtonJson = new JSONObject();
        excludeInlineKeyboardButtonJson.put("text","exclude");
        excludeInlineKeyboardButtonJson.put("callback_data", objectMapper.writeValueAsString(exclude));

        JSONObject quiteInlineKeyboardButtonJson = new JSONObject();
        quiteInlineKeyboardButtonJson.put("text","quite");
        quiteInlineKeyboardButtonJson.put("callback_data", objectMapper.writeValueAsString(quite));

        JSONArray inlineKeyboardButtonArrayJson = new JSONArray();
        inlineKeyboardButtonArrayJson.put(addToLearnInlineKeyboardButtonJson);
        inlineKeyboardButtonArrayJson.put(excludeInlineKeyboardButtonJson);
        inlineKeyboardButtonArrayJson.put(quiteInlineKeyboardButtonJson);

        JSONArray inlineKeyboardArrays = new JSONArray();
        inlineKeyboardArrays.put(inlineKeyboardButtonArrayJson);
        JSONObject mainObj = new JSONObject();
        mainObj.put("inline_keyboard", inlineKeyboardArrays);

        return mainObj;
    }
}
