package ru.flashcards.telegram.bot.db.dmlOps;

import ru.flashcards.telegram.bot.db.SelectWithParams;
import ru.flashcards.telegram.bot.db.Update;
import ru.flashcards.telegram.bot.db.dmlOps.dto.Flashcard;
import ru.flashcards.telegram.bot.db.dmlOps.dto.SendToLearnFlashcard;
import ru.flashcards.telegram.bot.db.dmlOps.dto.UserFlashcard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FlashcardDataHandler {
    /**
     * Карточки для заучивания
     */
    public List<SendToLearnFlashcard> getFlashcardsByWordToSuggestLearning(Long chatId, String flashcardWord) {
        return new SelectWithParams<SendToLearnFlashcard>(
                "select u.chat_id, fc.flashcard_id, fc.word, fc.description, fc.translation, fc.transcription from main.user u \n" +
                        " join lateral (\n" +
                        "     select f.id flashcard_id, f.word, f.description, f.translation, f.transcription from main.flashcard f\n" +
                        "        where not exists(select 1 from main.excepted_user_flashcard euf where euf.flashcard_id = f.id and euf.user_id = u.id) and\n" +
                        "              not exists(select 1 from main.user_flashcard uf where uf.word = f.word and uf.user_id = u.id) and \n" +
                        "              f.word = coalesce(?, f.word) \n" +
                        "              limit 1 \n" +
                        "    ) fc on true\n" +
                        "where u.chat_id = ? "
        ){
            @Override
            protected SendToLearnFlashcard rowMapper(ResultSet rs) throws SQLException {
                return new SendToLearnFlashcard(
                        rs.getLong("chat_id"),
                        rs.getLong("flashcard_id"),
                        rs.getString("description"),
                        rs.getString("transcription"),
                        rs.getString("translation"),
                        rs.getString("word")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, flashcardWord);
                preparedStatement.setLong(2, chatId);
                return preparedStatement;
            }
        }.getCollection();
    }

    /**
     * Карточки для заучивания
     */
    public List<SendToLearnFlashcard> getFlashcardsByCategoryToSuggestLearning(Long chatId, Long flashcardCategoryId) {
        return new SelectWithParams<SendToLearnFlashcard>(
                "select u.chat_id, fc.flashcard_id, fc.word, fc.description, fc.translation, fc.transcription from main.user u \n" +
                        " join lateral (\n" +
                        "     select f.id flashcard_id, f.word, f.description, f.translation, f.transcription from main.flashcard f\n" +
                        "        where not exists(select 1 from main.excepted_user_flashcard euf where euf.flashcard_id = f.id and euf.user_id = u.id) and\n" +
                        "              not exists(select 1 from main.user_flashcard uf where uf.word = f.word and uf.user_id = u.id) and \n" +
                        "              f.category_id = coalesce(?, f.category_id) \n" +
                        "              limit 1 \n" +
                        "    ) fc on true\n" +
                        "where u.chat_id = ? "
        ){
            @Override
            protected SendToLearnFlashcard rowMapper(ResultSet rs) throws SQLException {
                return new SendToLearnFlashcard(
                        rs.getLong("chat_id"),
                        rs.getLong("flashcard_id"),
                        rs.getString("description"),
                        rs.getString("transcription"),
                        rs.getString("translation"),
                        rs.getString("word")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardCategoryId);
                preparedStatement.setLong(2, chatId);
                return preparedStatement;
            }
        }.getCollection();
    }

    /**
     * Список примеров использования
     */
    public List<String> getExamplesByFlashcardId(Long flashcardId) {
        return new SelectWithParams<String>(
                "select concat(row_number() over () , '. ', example) as example  From main.flashcard_examples where flashcard_id = ? order by id"
        ){
            @Override
            protected String rowMapper(ResultSet rs) throws SQLException {
                return rs.getString("example");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardId);
                return preparedStatement;
            }
        }.getCollection();
    }

    public int editTranslation (Long chatId, String word, String translation) {
        return new Update("update main.user_flashcard uf set translation = ? from main.user u where uf.user_id = u.id and uf.word = ? and u.chat_id = ?"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, translation);
                preparedStatement.setString(2, word);
                preparedStatement.setLong(3, chatId);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Создание новой карточки
     */
    public int createFlashcard(Long chatId, String word, String description, String translation, String transcription) {
        return new Update(
                "insert into main.user_flashcard (id, word, description, translation, transcription, user_id) " +
                        "select nextval('main.common_seq'), ?, ?, ?, ?, (select id from main.user where chat_id = ?)  "
        ){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, word);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, translation);
                preparedStatement.setString(4, transcription);
                preparedStatement.setLong(5, chatId);
                return preparedStatement;
            }
        }.run();
    }

    public int createDefinition(Long chatId, String word, String description) {
        return new Update(
                "insert into main.user_flashcard (id, word, description, user_id, learned_date) " +
                        "select nextval('main.common_seq'), ?, ?, (select id from main.user where chat_id = ?), now() "
        ){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, word);
                preparedStatement.setString(2, description);
                preparedStatement.setLong(3, chatId);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Найти карточку пользователя по ид
     */
    public UserFlashcard findUserFlashcardById(Long flashcardId) {
        return new SelectWithParams<UserFlashcard>("select description, transcription, translation, word from main.user_flashcard where id = ?"){
            @Override
            protected UserFlashcard rowMapper(ResultSet rs) throws SQLException {
                return  new UserFlashcard(
                        rs.getString("description"),
                        rs.getString("transcription"),
                        rs.getString("translation"),
                        rs.getString("word")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardId);
                return preparedStatement;
            }
        }.getObject();
    }

    /**
     * Найти карточку по ид
     */
    public Flashcard findFlashcardById(Long flashcardId) {
        return new SelectWithParams<Flashcard>("select category_id, description, transcription, translation, word from main.flashcard where id = ?"){
            @Override
            protected Flashcard rowMapper(ResultSet rs) throws SQLException {
                return new Flashcard(
                        rs.getLong("category_id"),
                        rs.getString("description"),
                        rs.getString("transcription"),
                        rs.getString("translation"),
                        rs.getString("word")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardId);
                return preparedStatement;
            }
        }.getObject();
    }

    /**
     * Добавить карточку для изучения
     */
    public int addUserFlashcard(String word, String description, String transcription, String translation, Long categoryId, Long chatId) {
        return new Update(
                "insert into main.user_flashcard (id, word, description, transcription, translation, category_id, user_id)\n" +
                        "select nextval('main.flashcard_id_seq') ,?,?,?,?,?, (select id from main.user where chat_id = ?)"
        ){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, word);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, transcription);
                preparedStatement.setString(4, translation);
                preparedStatement.setLong(5, categoryId);
                preparedStatement.setLong(6, chatId);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Исключить карточку
     */
    public int exceptFlashcard (Long chatId, Long flashcardId) {
        return new Update(
                "insert into main.excepted_user_flashcard (user_id, flashcard_id) values ((select id from main.user where chat_id = ?),?)"
        ) {
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                preparedStatement.setLong(2, flashcardId);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Список примеров использования
     */
    public List<String> getExamplesByUserFlashcardId(Long userFlashcardId) {
        return new SelectWithParams<String>(
                "select example From main.flashcard_examples where flashcard_id = (select id from main.flashcard where word = " +
                        "(select word from main.user_flashcard where id = ?))"
        ){
            @Override
            protected String rowMapper(ResultSet rs) throws SQLException {
                return rs.getString("example");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, userFlashcardId);
                return preparedStatement;
            }
        }.getCollection();
    }
}
