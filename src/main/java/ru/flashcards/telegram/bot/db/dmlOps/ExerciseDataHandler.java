package ru.flashcards.telegram.bot.db.dmlOps;

import ru.flashcards.telegram.bot.db.Select;
import ru.flashcards.telegram.bot.db.SelectWithParams;
import ru.flashcards.telegram.bot.db.Update;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseFlashcard;
import ru.flashcards.telegram.bot.db.dmlOps.dto.ExerciseKind;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ExerciseDataHandler {
    /**
     * Список упражнений
     */
    public List<ExerciseFlashcard> getExercise() {
        return new Select<ExerciseFlashcard>(
                "select chat_id, word, code, description, transcription, user_flashcard_id, translation, example " +
                    "from main.next_exercise_queue where not learn_flashcard_lock") {
            @Override
            protected ExerciseFlashcard rowMapper(ResultSet rs) throws SQLException {
                return new ExerciseFlashcard(
                        rs.getLong("chat_id"),
                        rs.getString("word"),
                        rs.getString("code"),
                        rs.getString("description"),
                        rs.getString("transcription"),
                        rs.getLong("user_flashcard_id"),
                        rs.getString("translation"),
                        rs.getString("example")
                );
            }
        }.getCollection();
    }

    /**
     * Список случайных описаний
     */
    public List<String> getRandomDescriptions() {
        return new Select<String>("select description from main.random_flashcard"){
            @Override
            protected String rowMapper(ResultSet rs) throws SQLException {
                return rs.getString("description");
            }
        }.getCollection();
    }

    /**
     * Список случайных переводов
     */
    public List<String> getRandomTranslations() {
        return new Select<String>("select translation from main.random_flashcard"){
            @Override
            protected String rowMapper(ResultSet rs) throws SQLException {
                return rs.getString("translation");
            }
        }.getCollection();
    }

    /**
     * Список случайных слов
     */
    public List<String> getRandomWords() {
        return new Select<String>("select word from main.random_flashcard"){
            @Override
            protected String rowMapper(ResultSet rs) throws SQLException {
                return rs.getString("word");
            }
        }.getCollection();
    }

    /**
     * Блокировка отправки упражнений
     */
    public int setLock(Long chatId, Boolean value) {
        return new Update("update main.user set learn_flashcard_lock = ? where chat_id = ?"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBoolean(1, value);
                preparedStatement.setLong(2, chatId);
                return preparedStatement;
            }
        }.run();
    }

    public int insertExerciseResult (Long userFlashcardId, String exerciseKind, Boolean result) {
        return new Update("insert into main.done_learn_exercise_stat (id, user_flashcard_id, exercise_kind_id, is_correct_answer) values " +
                "(nextval('main.common_seq'), ?, (select id from main.learning_exercise_kind where code = ?), ?)"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, userFlashcardId);
                preparedStatement.setString(2, exerciseKind);
                preparedStatement.setBoolean(3, result);
                return preparedStatement;
            }
        }.run();
    }

    public ExerciseFlashcard getCurrentExercise(Long chatId) {
        return new SelectWithParams<ExerciseFlashcard>(
                "select chat_id, word, code, description, transcription, user_flashcard_id, translation, example " +
                    "from main.next_exercise_queue where chat_id = ?"){
            @Override
            protected ExerciseFlashcard rowMapper(ResultSet rs) throws SQLException {
                return new ExerciseFlashcard(
                        rs.getLong("chat_id"),
                        rs.getString("word"),
                        rs.getString("code"),
                        rs.getString("description"),
                        rs.getString("transcription"),
                        rs.getLong("user_flashcard_id"),
                        rs.getString("translation"),
                        rs.getString("example")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return preparedStatement;
            }
        }.getObject();
    }

    /**
     * Текущая порция слов для изучения
     */
    public List<String> getCurrentBatchFlashcardsByUser(Long chatId) {
        return new SelectWithParams<String>(
                "select word\n" +
                        "from (\n" +
                        "       select user_id,\n" +
                        "              id user_flashcard_id,\n" +
                        "              word,\n" +
                        "              row_number() over(partition by user_id\n" +
                        "       order by id) rn\n" +
                        "       from main.user_flashcard\n" +
                        "       where user_id = (select id from main.user where chat_id = ?) and\n" +
                        "             learned_date is null\n" +
                        "     ) x\n" +
                        "where rn <= 7"
        ){
            @Override
            protected String rowMapper(ResultSet rs) throws SQLException {
                return rs.getString("word");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return preparedStatement;
            }
        }.getCollection();
    }

    /**
     * Фиксация выученных карточек
     */
    public int refreshLearnedFlashcards() {
        return new Update("update main.user_flashcard a set learned_date = now() from main.learned_flashcards_stat b where a.id = b.user_flashcard_id"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Включить/отключить режим обучения
     */
    public int setLearnFlashcardState(Long chatId, Boolean value) {
        return new Update("update main.user set learn_flashcard_state = ? where chat_id = ?"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBoolean(1, value);
                preparedStatement.setLong(2, chatId);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Признак включенного режима обучения
     */
    public Boolean isLearnFlashcardState(Long chatId) {
        return new SelectWithParams<Boolean>("select learn_flashcard_state from main.user where chat_id = ?"){
            @Override
            protected Boolean rowMapper(ResultSet rs) throws SQLException {
                return rs.getBoolean("learn_flashcard_state");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return preparedStatement;
            }
        }.getObject();
    }

    /**
     * Изученные слова
     */
    public List<String> getLearnedFlashcards(Long chatId) {
        return new SelectWithParams<String>("select a.word From main.user_flashcard a, main.learned_flashcards_stat b, main.user u " +
                "where a.id = b.user_flashcard_id and a.user_id = u.id and u.chat_id = ?"){
            @Override
            protected String rowMapper(ResultSet rs) throws SQLException {
                return rs.getString("word");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return preparedStatement;
            }
        }.getCollection();
    }

    public Boolean existsExercise(Long chatId) {
        return new SelectWithParams<Boolean>("select exists(select 1 from main.user_flashcard a, main.user b " +
                "where a.user_id = b.id and a.learned_date is null and b.chat_id = ? limit 1) result"){
            @Override
            protected Boolean rowMapper(ResultSet rs) throws SQLException {
                return rs.getBoolean("result");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return preparedStatement;
            }
        }.getObject();
    }

    public List<ExerciseKind> getExerciseKindToEnable(Long chatId) {
        return new SelectWithParams<ExerciseKind>(
                "select code, name From main.learning_exercise_kind a where " +
                        "not exists (select 1 from main.user_exercise_settings b where a.id = b.exercise_kind_id and b.user_id = (select id from main.user where chat_id = ?)) order by a.order"
        ){
            @Override
            protected ExerciseKind rowMapper(ResultSet rs) throws SQLException {
                return new ExerciseKind(
                        rs.getString("code"),
                        rs.getString("name")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return preparedStatement;
            }
        }.getCollection();
    }

    public List<ExerciseKind> getExerciseKindToDisable(Long chatId) {
        return new SelectWithParams<ExerciseKind>(
                "select code, name From main.learning_exercise_kind a where " +
                        "exists (select 1 from main.user_exercise_settings b where a.id = b.exercise_kind_id and b.user_id = (select id from main.user where chat_id = ?)) order by a.order"
        ){
            @Override
            protected ExerciseKind rowMapper(ResultSet rs) throws SQLException {
                return new ExerciseKind(
                        rs.getString("code"),
                        rs.getString("name")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return preparedStatement;
            }
        }.getCollection();
    }

    public int deleteExerciseStat(Long flashcardId) {
        return new Update("delete from main.done_learn_exercise_stat where user_flashcard_id = ?") {
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardId);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Сбросить признак изучения карточки
     */
    public int returnToLearn (Long flashcardId) {
        return new Update("update main.user_flashcard set learned_date = null where id = ?") {
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, flashcardId);
                return preparedStatement;
            }
        }.run();
    }

    public int disableExcercise(Long chatId, String excerciseCode) {
        return new Update("delete from main.user_exercise_settings where user_id = (select id from main.user where chat_id = ?) and " +
                "exercise_kind_id = (select id from main.learning_exercise_kind where code = ?)") {
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                preparedStatement.setString(2, excerciseCode);
                return preparedStatement;
            }
        }.run();
    }

    public int enableExcercise(Long chatId, String excerciseCode) {
        return new Update("insert into main.user_exercise_settings (id, user_id, exercise_kind_id) values " +
                "(nextval('main.common_seq'), (select id from main.user where chat_id = ?), (select id from main.learning_exercise_kind where code = ?) )") {
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                preparedStatement.setString(2, excerciseCode);
                return preparedStatement;
            }
        }.run();
    }

    /**
     * Add flashcard for nearest learning
     */
    public int boostUserFlashcardPriority(Long userFlashcardId) {
        return new Update("update main.user_flashcard set nearest_training = 1 where id = ?"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, userFlashcardId);
                return preparedStatement;
            }
        }.run();
    }
}
