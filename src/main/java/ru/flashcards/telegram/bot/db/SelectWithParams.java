package ru.flashcards.telegram.bot.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class SelectWithParams<T> {
    private String query;

    public SelectWithParams(String sql) {
        query = sql;
    }

    public List<T> getCollection(){
        List<T> list = new ArrayList<>();

        try(Connection connection = DataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement = parameterMapper(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            T object;

            while (rs.next()) {
                object = rowMapper(rs);
                list.add(object);
            }
        } catch (SQLException e) {
            DBExceptionHandler.printSQLException(e);
        }

        return list;
    }

    public T getObject(){
        T object = null;

        try(Connection connection = DataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement = parameterMapper(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                object = rowMapper(rs);
            }
        } catch (SQLException e) {
            DBExceptionHandler.printSQLException(e);
        }

        return object;
    }

    protected abstract T rowMapper(ResultSet rs) throws SQLException;

    protected abstract PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException;

}
