package ru.flashcards.telegram.bot.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.flashcards.telegram.bot.db.dmlOps.ExerciseDataHandler;

public class StartLearningCommand extends BotCommand {
    private ExerciseDataHandler exerciseDataHandler;

    public StartLearningCommand(String commandIdentifier, String description, ExerciseDataHandler handler) {
        super(commandIdentifier, description);
        this.exerciseDataHandler = handler;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        if (exerciseDataHandler.existsExercise(user.getId())){
            //enable learn mode
            exerciseDataHandler.setLearnFlashcardState(user.getId(), true);
            //get new exercise
            exerciseDataHandler.setLock(user.getId(), false);
        } else {
            SendMessage sendMessage = new SendMessage();

            sendMessage.setText("All flashcards have learned.");
            sendMessage.setChatId(String.valueOf(chat.getId()));
            try {
                absSender.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
