package ru.flashcards.telegram.bot.botapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.flashcards.telegram.bot.botapi.swiper.SwiperParams;

public class CallbackData {
    @JsonProperty("c")
    private String command;
    @JsonProperty("id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long entityId;
    @JsonProperty("code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String entityCode;
    @JsonProperty("sw")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SwiperParams swiper;

    public CallbackData() {
    }

    public CallbackData(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public SwiperParams getSwiper() {
        return swiper;
    }

    public void setSwiper(SwiperParams swiper) {
        this.swiper = swiper;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }
}
