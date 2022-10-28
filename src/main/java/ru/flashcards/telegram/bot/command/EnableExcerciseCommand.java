package ru.flashcards.telegram.bot.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.flashcards.telegram.bot.botapi.CallbackData;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseKind;

import java.util.ArrayList;
import java.util.List;

import static ru.flashcards.telegram.bot.botapi.Literals.ENABLE_EXCERCISE;

public class EnableExcerciseCommand extends BotCommand {
    private ExerciseDataHandler exerciseDataHandler;
    private ObjectMapper objectMapper = new ObjectMapper();

    public EnableExcerciseCommand(String commandIdentifier, String description, ExerciseDataHandler exerciseDataHandler) {
        super(commandIdentifier, description);
        this.exerciseDataHandler = exerciseDataHandler;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        super.processMessage(absSender, message, arguments);
        Long chatId = message.getChatId();
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(String.valueOf(message.getChatId()));

        List<ExerciseKind> exerciseKinds  = exerciseDataHandler.getExerciseKindToEnable(chatId);
        if (!exerciseKinds.isEmpty()) {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

            exerciseKinds.forEach(v->{
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(v.getName());
                CallbackData callbackData = new CallbackData(ENABLE_EXCERCISE);
                callbackData.setEntityCode(v.getCode());
                try {
                    button.setCallbackData(objectMapper.writeValueAsString(callbackData));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                rowInline.add(button);
                rowsInline.add(rowInline);
            });
            markupInline.setKeyboard(rowsInline);
            replyMessage.setText("Available exercises, tap to enable: ");
            replyMessage.setReplyMarkup(markupInline);
        }
        else {
            replyMessage.setText("All exercises have been enabled");
        }

        try {
            absSender.execute(replyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
    }
}
