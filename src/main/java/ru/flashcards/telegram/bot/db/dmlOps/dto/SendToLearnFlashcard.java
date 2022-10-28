package ru.flashcards.telegram.bot.db.dmlOps.dto;

public class SendToLearnFlashcard {
    private Long userId;
    private Long flashcardId;
    private String description;
    private String transcription;
    private String translation;
    private String word;

    public SendToLearnFlashcard(Long userId, Long flashcardId, String description, String transcription, String translation, String word) {
        this.userId = userId;
        this.flashcardId = flashcardId;
        this.description = description;
        this.transcription = transcription;
        this.translation = translation;
        this.word = word;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(Long flashcardId) {
        this.flashcardId = flashcardId;
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
