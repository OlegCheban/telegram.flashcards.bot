package ru.flashcards.telegram.bot.db.dmlOps.dto;

import ru.flashcards.telegram.bot.botapi.ExerciseKind;

public class ExerciseFlashcard {
    Long chatId;
    String word;
    ExerciseKind exerciseKindCode;
    String description;
    String transcription;
    Long userFlashcardId;
    String translation;
    String example;
    public ExerciseFlashcard(Long chatId, String word, ExerciseKind exerciseKindCode, String description, String transcription, Long userFlashcardId, String translation, String example) {
        this.chatId = chatId;
        this.word = word;
        this.exerciseKindCode = exerciseKindCode;
        this.description = description;
        this.transcription = transcription;
        this.userFlashcardId = userFlashcardId;
        this.translation = translation;
        this.example = example;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ExerciseKind getExerciseKindCode() {
        return exerciseKindCode;
    }

    public void setExerciseKindCode(ExerciseKind exerciseKindCode) {
        this.exerciseKindCode = exerciseKindCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public Long getUserFlashcardId() {
        return userFlashcardId;
    }

    public void setUserFlashcardId(Long userFlashcardId) {
        this.userFlashcardId = userFlashcardId;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
