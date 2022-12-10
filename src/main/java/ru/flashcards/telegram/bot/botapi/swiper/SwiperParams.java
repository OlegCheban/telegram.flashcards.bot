package ru.flashcards.telegram.bot.botapi.swiper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwiperParams {
    @JsonProperty("ch")
    private String charCond;
    @JsonProperty("p")
    private String prc;

    public SwiperParams() {
    }

    public SwiperParams(String charCond, String prc) {
        this.charCond = charCond;
        this.prc = prc;
    }

    public String getCharCond() {
        return charCond;
    }

    public void setCharCond(String charCond) {
        this.charCond = charCond;
    }

    public String getPrc() {
        return prc;
    }

    public void setPrc(String prc) {
        this.prc = prc;
    }
}
