package ru.flashcards.telegram.bot.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.flashcards.telegram.bot.botapi.handlers.learn.exercises.common.ExerciseProvider;
import ru.flashcards.telegram.bot.db.dmlOps.DataLayerObject;

public class StartLearningCommand extends BotCommand {
    private DataLayerObject dataLayer;

    public StartLearningCommand(String commandIdentifier, String description, DataLayerObject dataLayerObject) {
        super(commandIdentifier, description);
        this.dataLayer = dataLayerObject;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            if (dataLayer.existsExercise(user.getId())){
                //enable learn mode
                dataLayer.setLearnFlashcardState(user.getId(), true);
                //send an exercise
                ExerciseProvider exerciseProvider = new ExerciseProvider(dataLayer);
                absSender.execute(exerciseProvider.newExercise(chat.getId()));
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("All flashcards have learned.");
                sendMessage.setChatId(String.valueOf(chat.getId()));
                absSender.execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
