package ru.flashcards.telegram.bot.db.dmlOps.dto;

import java.time.LocalDateTime;

public class UserFlashcardPushMono {
    private Long userFlashcardId;
    private String word;
    private String description;
    private Long userId;
    private Long notificationInterval;
    private LocalDateTime lastPushTimestamp;
    private String transcription;

    public UserFlashcardPushMono(Long userFlashcardId, String word, String description, Long userId, Long notificationInterval, LocalDateTime lastPushTimestamp, String transcription) {
        this.userFlashcardId = userFlashcardId;
        this.word = word;
        this.description = description;
        this.userId = userId;
        this.notificationInterval = notificationInterval;
        this.lastPushTimestamp = lastPushTimestamp;
        this.transcription = transcription;
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

    public Long getNotificationInterval() {
        return notificationInterval;
    }

    public void setNotificationInterval(Long notificationInterval) {
        this.notificationInterval = notificationInterval;
    }

    public LocalDateTime getLastPushTimestamp() {
        return lastPushTimestamp;
    }

    public void setLastPushTimestamp(LocalDateTime lastPushTimestamp) {
        this.lastPushTimestamp = lastPushTimestamp;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }
}
