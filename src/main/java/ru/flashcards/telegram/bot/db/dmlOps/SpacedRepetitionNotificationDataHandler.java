package ru.flashcards.telegram.bot.db.dmlOps;

import ru.flashcards.telegram.bot.db.*;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcardSpacedRepetitionNotification;

import java.sql.*;
import java.util.List;

public class SpacedRepetitionNotificationDataHandler {
    /**
     * Проверка на необходимость обновления очереди отправки интервальных уведомлений
     */
    public Boolean isPushQueueUpToCurrentDate() {
        return new SelectWithParams<Boolean>(
                "select exists(select 1 from main.interval_repetition_queue where last_refresh = current_date limit 1) result"){
            @Override
            protected Boolean rowMapper(ResultSet rs) throws SQLException {
                return rs.getBoolean("result");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                return preparedStatement;
            }
        }.getObject();
    }

    /**
     * Обновления очереди отправки интервальных уведомлений
     */
    public void refreshIntervalNotification() {
        try(Connection connection = DataSource.getConnection()) {
            PreparedStatement call = connection.prepareCall("refresh materialized view main.interval_repetition_queue");
            call.execute();
        } catch (SQLException e) {
            DBExceptionHandler.printSQLException(e);
        }
    }

    /**
     * Очередь интервальных повторений
     */
    public List<UserFlashcardSpacedRepetitionNotification> getUserFlashcardsSpacedRepetitionNotification() {
        return new Select<UserFlashcardSpacedRepetitionNotification>(
                "SELECT a.user_flashcard_id, a.word, a.description, a.user_id, a.notification_date, a.transcription, a.prc " +
                        " FROM main.interval_repetition_queue a, main.user_flashcard uf where " +
                        " a.user_flashcard_id = uf.id and uf.learned_date is not null and " +
                        " not exists (select 1 from main.flashcard_push_history b where a.user_flashcard_id = b.flashcard_id and cast(a.notification_date as date) = cast(b.push_date as date))  " +
                        "  order by a.notification_date") {
            @Override
            protected UserFlashcardSpacedRepetitionNotification rowMapper(ResultSet rs) throws SQLException {
                Timestamp notificationDate = rs.getTimestamp("notification_date");
                return new UserFlashcardSpacedRepetitionNotification(
                        rs.getLong("user_flashcard_id"),
                        rs.getString("word"),
                        rs.getString("description"),
                        rs.getLong("user_id"),
                        notificationDate != null ? notificationDate.toLocalDateTime() : null,
                        rs.getString("transcription"),
                        rs.getInt("prc")
                );
            }
        }.getCollection();
    }

    /**
     * Добавление факта отправки уведомления для интервальных уведомлений
     */
    public int addFlashcardPushHistory(Long flashcardId) {
        return new Update("insert into main.flashcard_push_history (id, flashcard_id) values (nextval('main.common_seq'), ?)") {
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardId);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Удаление истории отправки интервальных уведомлений
     */
    public int deleteSpacedRepetitionHistory(Long flashcardId) {
        return new Update("delete from main.flashcard_push_history where flashcard_id = ?") {
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardId);
                return preparedStatement;
            }
        }.run();
    }
}
