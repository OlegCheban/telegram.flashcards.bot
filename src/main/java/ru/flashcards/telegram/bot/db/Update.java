package ru.flashcards.telegram.bot.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Update {
    private String query;

    public Update(String sql) {
        query = sql;
    }

    public int run (){
        try(Connection connection = DataSource.getConnection()) {
            return exec(connection);
        } catch (SQLException e) {
            DBExceptionHandler.printSQLException(e);
        }
        return -1;
    }

    public int run (Connection connection){
        try {
            return exec(connection);
        } catch (SQLException e) {
            DBExceptionHandler.printSQLException(e);
        }
        return -1;
    }

    private int exec (Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement = parameterMapper(preparedStatement);
        return preparedStatement.executeUpdate();
    }

    protected abstract PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException;
}
