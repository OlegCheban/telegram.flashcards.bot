package ru.flashcards.telegram.bot.db.dmlOps.dto;

import java.time.LocalDateTime;

public class UserFlashcardSpacedRepetitionNotification {
    private Long userFlashcardId;
    private String word;
    private String description;
    private Long userId;
    private LocalDateTime notificationDate;
    private String transcription;
    private int prc;

    public UserFlashcardSpacedRepetitionNotification(Long userFlashcardId, String word, String description, Long userId, LocalDateTime notificationDate, String transcription, int prc) {
        this.userFlashcardId = userFlashcardId;
        this.word = word;
        this.description = description;
        this.userId = userId;
        this.notificationDate = notificationDate;
        this.transcription = transcription;
        this.prc = prc;
    }

    public Long getUserFlashcardId() {
        return userFlashcardId;
    }

    public void setUserFlashcardId(Long userFlashcardId) {
        this.userFlashcardId = userFlashcardId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public int getPrc() {
        return prc;
    }

    public void setPrc(int prc) {
        this.prc = prc;
    }
}
