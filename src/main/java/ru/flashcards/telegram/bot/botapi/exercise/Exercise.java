package ru.flashcards.telegram.bot.botapi.exercise;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.utils.Lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static ru.flashcards.telegram.bot.botapi.Literals.*;
import static ru.flashcards.telegram.bot.botapi.Literals.COMPLETE_THE_GAPS;

public class Exercise {
    private DataLayerObject dataLayer;

    public Exercise(DataLayerObject dataLayer) {
        this.dataLayer = dataLayer;
    }

    public BotApiMethod<?> newExercise (Long chatId){
        ExerciseFlashcard currentExercise = dataLayer.getExercise(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<String> wrongAnswers = null;

        if (currentExercise.getExerciseCode().equals(MEMORISED)) {
            sendMessage.setText("*" + currentExercise.getWord() + "* \\[" + currentExercise.getTranscription() + "]\n" + currentExercise.getDescription() + "\n\n*Translation:* " + currentExercise.getTranslation());
            replyKeyboardMarkup.setKeyboard(memorisedKeyboard());

        } else if (currentExercise.getExerciseCode().equals(CHECK_DESCRIPTION)){
            wrongAnswers = dataLayer.getRandomDescriptions();
            sendMessage.setText("Choose correct description for flashcard *" + currentExercise.getWord() + "* \\[" + currentExercise.getTranscription() + "]\n\n");
            replyKeyboardMarkup.setKeyboard(answersKeyboard(wrongAnswers, currentExercise.getDescription()));

        } else if (currentExercise.getExerciseCode().equals(CHECK_TRANSLATION)){
            wrongAnswers = dataLayer.getRandomTranslations();
            sendMessage.setText("Choose correct translation for flashcard *" + currentExercise.getWord() + "* \\[" + currentExercise.getTranscription() + "]\n\n");
            replyKeyboardMarkup.setKeyboard(answersKeyboard(wrongAnswers, currentExercise.getTranslation()));

        } else if (currentExercise.getExerciseCode().equals(CHECK_SPELLING) || currentExercise.getExerciseCode().equals(CHECK_SPELLING_WITH_HELPS)){
            List<KeyboardRow> keyboard = new ArrayList<>();
            keyboard.add(createButton(STOP_LEARNING));
            String messageText = String.format("Write the correct spelling for flashcard *%s*", currentExercise.getTranslation());

            if (currentExercise.getExerciseCode().equals(CHECK_SPELLING_WITH_HELPS)) {
                messageText = messageText + "\n\nHint: " + hideLetters(currentExercise.getWord()).replaceAll ("\\*","\\\\*");
            }
            sendMessage.setText(messageText);
            replyKeyboardMarkup.setKeyboard(keyboard);

        } else if (currentExercise.getExerciseCode().equals(COMPLETE_THE_GAPS)){
            wrongAnswers = dataLayer.getRandomWords();
            sendMessage.setText("Complete the gaps in sentence below. \n\n" + currentExercise.getExample().replaceAll("\\*([a-zA-Z]+)\\*", "\\\\_\\\\_\\\\_\\\\_"));
            replyKeyboardMarkup.setKeyboard(answersKeyboard(wrongAnswers, currentExercise.getWord()));
        }

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
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

    private List<KeyboardRow> memorisedKeyboard(){
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createButton(MEMORISED));
        keyboard.add(createButton(STOP_LEARNING));

        return keyboard;
    }

    private List<KeyboardRow> answersKeyboard(List<String> wrongAnswersList, String correctAnswer){
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
        final int lastButton = 3;
        List<KeyboardRow> keyboard = new ArrayList<>();

        wrongAnswersList.forEach(Lambda.forEachWithCounter( (i, v) -> {
            if (i == randomNum){
                keyboard.add(createButton(correctAnswer));
            }
            keyboard.add(createButton(v));

            if (i == wrongAnswersList.size() && randomNum == lastButton){
                keyboard.add(createButton(correctAnswer));
            }
        }));
        keyboard.add(createButton(STOP_LEARNING));

        return keyboard;
    }

    private KeyboardRow createButton(String text){
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(text));

        return row;
    }
}
