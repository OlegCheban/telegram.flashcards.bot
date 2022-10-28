package ru.flashcards.telegram.bot.botapi.swiper;

public class SwiperParams {
    private String charCond;

    public SwiperParams() {
    }

    public SwiperParams(String charCond) {
        this.charCond = charCond;
    }

    public String getCharCond() {
        return charCond;
    }

    public void setCharCond(String charCond) {
        this.charCond = charCond;
    }
}
