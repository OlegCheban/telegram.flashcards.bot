package ru.flashcards.telegram.bot.db.dmlOps.dto;

public class ExerciseKind {
    String code;
    String name;

    public ExerciseKind(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
