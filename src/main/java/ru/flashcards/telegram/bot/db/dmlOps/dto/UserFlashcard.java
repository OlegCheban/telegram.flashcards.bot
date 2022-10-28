package ru.flashcards.telegram.bot.db.dmlOps.dto;

public class UserFlashcard {
    private String description;
    private String transcription;
    private String translation;
    private String word;

    public UserFlashcard(String description, String transcription, String translation, String word) {
        this.description = description;
        this.transcription = transcription;
        this.translation = translation;
        this.word = word;
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

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
