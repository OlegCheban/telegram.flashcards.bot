package ru.flashcards.telegram.bot.db.dmlOps;

import ru.flashcards.telegram.bot.db.*;
import ru.flashcards.telegram.bot.db.dmlOps.dto.*;

import java.sql.*;
import java.util.List;

import static ru.flashcards.telegram.bot.botapi.Literals.RANDOM_NOTIFICATION_INTERVAL;

public class DataLayerObject {
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
                                "with\n" +
                                "usr as\n" +
                                "(\n" +
                                "\tselect u.id\n" +
                                "    from main.user u\n" +
                                "    where u.chat_id = ?\n" +
                                ")\n" +
                                "select nextval('main.common_seq'),\n" +
                                "       usr.id,\n" +
                                "       a.id\n" +
                                "from main.learning_exercise_kind a,\n" +
                                "     usr\n" +
                                "where not exists (\n" +
                                "                   select 1\n" +
                                "                   from main.user_exercise_settings s\n" +
                                "                   where s.exercise_kind_id = a.id and\n" +
                                "                         s.user_id = usr.id\n" +
                                "      )") {
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

    public UserFlashcard getUserFlashcardForWateringSession(Long chatId) {
        return new SelectWithParams<UserFlashcard>(
                "select a.id, a.description, a.transcription, a.translation, a.word from main.user_flashcard a, main.user b " +
                        "where a.user_id = b.id and b.chat_id = ? and a.learned_date is not null order by a.watering_session_date nulls first, a.id limit 1") {
            @Override
            protected UserFlashcard rowMapper(ResultSet rs) throws SQLException {
                return new UserFlashcard(
                        rs.getLong("id"),
                        rs.getString("description"),
                        rs.getString("transcription"),
                        rs.getString("translation"),
                        rs.getString("word")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return preparedStatement;
            }
        }.getObject();
    }

    public int finishedLastFlashcard(Long userFlashcardId) {
        return new Update("update main.user_flashcard set watering_session_date = now() where id = ?"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, userFlashcardId);
                return preparedStatement;
            }
        }.run();
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



    /**
     * Текущая порция слов для изучения
     */
    public List<String> getCurrentBatchFlashcardsByUser(Long chatId) {
        return new SelectWithParams<String>(
                "select x.word\n" +
                        "from (\n" +
                        "       select uf.user_id,\n" +
                        "              uf.id user_flashcard_id,\n" +
                        "              uf.word,\n" +
                        "              u.cards_per_training,\n" +
                        "              row_number() over(partition by uf.user_id\n" +
                        "       order by uf.nearest_training desc, uf.id) rn\n" +
                        "       from main.user_flashcard uf\n" +
                        "          join main.user u on uf.user_id = u.id\n" +
                        "          join main.flashcard f on f.word::text = uf.word::text\n" +
                        "       where u.chat_id = ? and\n" +
                        "             uf.learned_date is null\n" +
                        "     ) x\n" +
                        "where x.rn <= x.cards_per_training"
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

    public int setWateringSessionMode(Long chatId, Boolean value) {
        return new Update("update main.user set watering_session = ? where chat_id = ?"){
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

    public Boolean isWateringSession(Long chatId) {
        return new SelectWithParams<Boolean>("select watering_session from main.user where chat_id = ?"){
            @Override
            protected Boolean rowMapper(ResultSet rs) throws SQLException {
                return rs.getBoolean("watering_session");
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

    public Boolean existsLearnedFlashcards(Long chatId) {
        return new SelectWithParams<Boolean>("select exists(select 1 from main.user_flashcard a, main.user b " +
                "where a.user_id = b.id and b.chat_id = ? and a.learned_date is not null limit 1) result"){
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

    public int editTranslation (Long flashcardId, String translation) {
        return new Update("update main.user_flashcard uf set translation = ? where uf.id = ? "){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, translation);
                preparedStatement.setLong(2, flashcardId);
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
        return new SelectWithParams<UserFlashcard>("select id, description, transcription, translation, word from main.user_flashcard where id = ?"){
            @Override
            protected UserFlashcard rowMapper(ResultSet rs) throws SQLException {
                return  new UserFlashcard(
                        rs.getLong("id"),
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

    public UserFlashcard findUserFlashcardByName(Long chatId, String name) {
        return new SelectWithParams<UserFlashcard>("select a.id, a.description, a.transcription, a.translation, a.word " +
                " from main.user_flashcard a, main.user b where a.user_id = b.id and b.chat_id = ? and a.word = ? "){
            @Override
            protected UserFlashcard rowMapper(ResultSet rs) throws SQLException {
                return  new UserFlashcard(
                        rs.getLong("id"),
                        rs.getString("description"),
                        rs.getString("transcription"),
                        rs.getString("translation"),
                        rs.getString("word")
                );
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                preparedStatement.setString(2, name);
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

    public int setTrainingFlashcardsQuantity (Integer qty, Long chatId) {
        return new Update("update main.user set cards_per_training = ? where chat_id = ?"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, qty);
                preparedStatement.setLong(2, chatId);
                return preparedStatement;
            }
        }.run();
    }

    public int setWateringSessionReplyTime (Integer seconds, Long chatId) {
        return new Update("update main.user set watering_session_reply_time = ? where chat_id = ?"){
            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, seconds);
                preparedStatement.setLong(2, chatId);
                return preparedStatement;
            }
        }.run();
    }

    public int getWateringSessionReplyTime (Long chatId){
        return new SelectWithParams<Integer>(
                "select watering_session_reply_time from main.user where chat_id = ?"){
            @Override
            protected Integer rowMapper(ResultSet rs) throws SQLException {
                return rs.getInt("watering_session_reply_time");
            }

            @Override
            protected PreparedStatement parameterMapper(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, chatId);
                return  preparedStatement;
            }
        }.getObject();
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
                        "                         prc, " +
                        "                         nearest_training " +
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
                        rs.getInt("prc"),
                        rs.getInt("nearest_training")
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
