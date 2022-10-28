package ru.flashcards.telegram.bot.db.dmlOps;

import ru.flashcards.telegram.bot.db.DataSource;
import ru.flashcards.telegram.bot.db.Update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static ru.flashcards.telegram.bot.botapi.Literals.RANDOM_NOTIFICATION_INTERVAL;

public class UserDataHandler {
    public void registerUser(Long chatId, String username) {
        try(Connection connection = DataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                new Update(
                        "insert into main.user (id, name, notification_interval, chat_id) " +
                                "select nextval('main.common_seq'), ?, ?, ? where not exists (select 1 from main.user where chat_id = ?) ") {
                    @Override
                    protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setString(1, username);
                        preparedStatement.setLong(2, RANDOM_NOTIFICATION_INTERVAL);
                        preparedStatement.setLong(3, chatId);
                        preparedStatement.setLong(4, chatId);
                        return preparedStatement;
                    }
                }.run(connection);

                new Update(
                        "insert into main.user_exercise_settings (id, user_id, exercise_kind_id)\n" +
                                "select nextval('main.common_seq'), (select id from main.user where chat_id = ?), id from main.learning_exercise_kind ") {
                    @Override
                    protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, chatId);
                        return preparedStatement;
                    }
                }.run(connection);

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
