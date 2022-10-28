package ru.flashcards.telegram.bot.db.dmlOps;

import ru.flashcards.telegram.bot.db.SelectWithParams;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SwiperFlashcard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SwiperDataHandler {
    public Long getFirstSwiperFlashcard(Long chatId, String characterCondition) {
        return new SelectWithParams<Long>("select min(id) id from main.swiper_flashcards where chat_id = ? and (? is null or lower(word) like lower(?) || '%') "){
            @Override
            protected Long rowMapper(ResultSet rs) throws SQLException {
                return rs.getLong("id");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                preparedStatement.setString(2, characterCondition);
                preparedStatement.setString(3, characterCondition);
                return preparedStatement;
            }
        }.getObject();
    }

    /**
     * Карточки для свайпера
     */
    public SwiperFlashcard getSwiperFlashcard(Long chatId, Long currentFlashcardId, String characterCondition) {
        return new SelectWithParams<SwiperFlashcard>(
                "select * from (" +
                        "                  select lag(id) over (order by id)  prev_id, " +
                        "                         id                          current_id, " +
                        "                         lead(id) over (order by id) next_id, " +
                        "                         word, " +
                        "                         description, " +
                        "                         translation, " +
                        "                         transcription, " +
                        "                         prc " +
                        "                      from main.swiper_flashcards " +
                        "                      where chat_id = ? and (? is null or lower(word) like lower(?) || '%') " +
                        "                      order by id " +
                        "              ) x where x.current_id = ? "
        ){
            @Override
            protected SwiperFlashcard rowMapper(ResultSet rs) throws SQLException {
                return new SwiperFlashcard(
                        rs.getLong("prev_id"),
                        rs.getLong("next_id"),
                        rs.getLong("current_id"),
                        rs.getString("word"),
                        rs.getString("description"),
                        rs.getString("translation"),
                        rs.getString("transcription"),
                        rs.getInt("prc")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                preparedStatement.setString(2, characterCondition);
                preparedStatement.setString(3, characterCondition);
                preparedStatement.setLong(4, currentFlashcardId);
                return preparedStatement;
            }
        }.getObject();
    }
}
