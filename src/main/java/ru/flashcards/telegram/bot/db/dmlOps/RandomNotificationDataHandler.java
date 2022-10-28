package ru.flashcards.telegram.bot.db.dmlOps;

import ru.flashcards.telegram.bot.db.Select;
import ru.flashcards.telegram.bot.db.Update;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcardPushMono;

import java.sql.*;
import java.util.List;

public class RandomNotificationDataHandler {
    /**
     * Разовые напоминания
     */
    public List<UserFlashcardPushMono> getUserFlashcardsRandomNotification() {
        return new Select<UserFlashcardPushMono>("SELECT user_flashcard_id, word, description, user_id, notification_interval, last_push_timestamp, transcription  FROM main.flashcards_push_mono"){
            @Override
            protected UserFlashcardPushMono rowMapper(ResultSet rs) throws SQLException {
                Timestamp lastPushTimestamp = rs.getTimestamp("last_push_timestamp");
                return new UserFlashcardPushMono(
                        rs.getLong("user_flashcard_id"),
                        rs.getString("word"),
                        rs.getString("description"),
                        rs.getLong("user_id"),
                        rs.getLong("notification_interval"),
                        lastPushTimestamp != null ? lastPushTimestamp.toLocalDateTime() : null,
                        rs.getString("transcription")
                );
            }
        }.getCollection();
    }

    /**
     * Установить интервал отправки уведомлений
     */
    public int setNotificationInterval (Integer minQty, Long chatId) {
        return new Update("update main.user set notification_interval = ? where chat_id = ?"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, minQty);
                preparedStatement.setLong(2, chatId);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Регистрация факта отправки уведомления для интервальных уведомлений
     */
    public int updatePushTimestampById(Long flashcardId) {
        return new Update("update main.user_flashcard set push_timestamp = current_timestamp WHERE id = ? "){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardId);
                return preparedStatement;
            }
        }.run();
    }

}
