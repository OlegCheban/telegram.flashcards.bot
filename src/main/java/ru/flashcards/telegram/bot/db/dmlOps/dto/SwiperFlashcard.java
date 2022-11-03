package ru.flashcards.telegram.bot.db.dmlOps.dto;

public class SwiperFlashcard {
    private Long prevId;
    private Long nextId;
    private Long currentId;
    private String word;
    private String description;
    private String translation;
    private String transcription;
    private int learnPrc;
    private int nearestTraining;

    public SwiperFlashcard(Long prevId, Long nextId, Long currentId, String word, String description, String translation, String transcription, int learnPrc, int nearestTraining) {
        this.prevId = prevId;
        this.nextId = nextId;
        this.currentId = currentId;
        this.word = word;
        this.description = description;
        this.translation = translation;
        this.transcription = transcription;
        this.learnPrc = learnPrc;
        this.nearestTraining = nearestTraining;
    }

    public Long getPrevId() {
        return prevId;
    }

    public void setPrevId(Long prevId) {
        this.prevId = prevId;
    }

    public Long getNextId() {
        return nextId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }

    public Long getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
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

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public int getLearnPrc() {
        return learnPrc;
    }

    public void setLearnPrc(int learnPrc) {
        this.learnPrc = learnPrc;
    }

    public int getNearestTraining() {
        return nearestTraining;
    }

    public void setNearestTraining(int nearestTraining) {
        this.nearestTraining = nearestTraining;
    }
}
