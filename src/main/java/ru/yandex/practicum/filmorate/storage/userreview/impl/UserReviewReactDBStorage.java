package ru.yandex.practicum.filmorate.storage.userreview.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserReviewReact;
import ru.yandex.practicum.filmorate.storage.userreview.UserReviewReactStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository("UserLikeReviewDBStorage")
public class UserReviewReactDBStorage implements UserReviewReactStorage {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public UserReviewReactDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Создать запись в БД о реакции пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param userReviewReact реакция пользователя.
     * @param reviewId        ID отзыва.
     * @return UserLikedReview реакция пользователя.
     * <p>Готово.</p>
     */
    @Override
    public UserReviewReact addUserReviewReact(UserReviewReact userReviewReact, Integer reviewId) {
        removeUserReviewReactByReviewIdAndUserId(reviewId, userReviewReact.getUserId());
        final Integer userId = userReviewReact.getUserId();
        
        String sqlQuery = "insert into REVIEW_LIKE_DISLIKE " +
                "(REVIEW_ID, USER_ID, IS_LIKE) " +
                "VALUES (?, ?, ?)";
        
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery);
            stmt.setInt(1, reviewId);
            stmt.setInt(2, userId);
            stmt.setBoolean(3, userReviewReact.getIsLike());
            return stmt;
        });
        log.info(String.format("Создана запись о реакции с ID = %d пользователя с ID = %d", reviewId, userId));
        return userReviewReact;
    }
    
    /**
     * Получить список реакций пользователей на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @return список реакций (id юзера, isLike).
     */
    @Override
    public List<UserReviewReact> getUserLikeReviewsByReviewId(Integer reviewId) {
        String sqlQuery = "select * from review_like_dislike where review_id = ?";
        List<UserReviewReact> result = jdbcTemplate.query(sqlQuery, new UserReviewReactMapper(), reviewId);
        String message = "Получен список реакций пользователей на отзыв с ID = {}.";
        log.info(message, reviewId);
        return result;
    }
    
    /**
     * Получить реакцию пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     * @return реакция (id юзера, isLike) или 'null'.
     */
    @Override
    public UserReviewReact getUserReviewReactByReviewIdAndUserId(Integer reviewId, Integer userId) {
        String sqlQuery = "select * from REVIEW_LIKE_DISLIKE where REVIEW_ID = ? and USER_ID = ?";
        UserReviewReact result = jdbcTemplate.queryForObject(sqlQuery, new UserReviewReactMapper(), reviewId, userId);
        String message = "Получена реакция пользователя с ID = {} на отзыв с ID = {}.";
        log.info(message, reviewId, userId);
        return result;
    }
    
    /**
     * Поставить лайк/дизлайк отзыву с reviewId пользователем userId.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     * @param isLike   True - лайк, False - дизлайк.
     */
    @Override
    public void setLikeForReview(Integer reviewId, Integer userId, Boolean isLike) {
        String sqlQueryForDelete = "DELETE FROM REVIEW_LIKE_DISLIKE WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQueryForDelete, reviewId, userId);
        String sqlQueryForAddLike = "MERGE INTO REVIEW_LIKE_DISLIKE (review_id, user_id, is_like)" +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQueryForAddLike, reviewId, userId, isLike);
        if (isLike) {
            log.info("Установлен лайк отзыву с ID = {} пользователем с ID = {}.", reviewId, userId);
        } else {
            log.info("Установлен дизлайк отзыву с ID = {} пользователем с ID = {}.", reviewId, userId);
        }
    }
    
    
    /**
     * Удалить реакцию пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     */
    @Override
    public void removeUserReviewReactByReviewIdAndUserId(Integer reviewId, Integer userId) {
        String sqlQuery = "delete from REVIEW_LIKE_DISLIKE where REVIEW_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
        log.info(String.format("Выполнено удаление реакции с ID = %d пользователя с ID = %d.", reviewId, userId));
    }
    
    /**
     * Проверить наличие оценки пользователем с ID отзыву с ID.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     */
    @Override
    public Boolean isExistUserReviewReactInDB(Integer reviewId, Integer userId) {
        String sql = "select count(REVIEW_ID) from REVIEW_LIKE_DISLIKE where REVIEW_ID = ? AND USER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId);
        return (count != null) && (count == 1);
    }
    
    /**
     * Удалить все реакции пользователей на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     */
    @Override
    public void removeUserReviewReactByReviewId(Integer reviewId) {
        String sql = "delete from REVIEW_LIKE_DISLIKE where REVIEW_ID = ?";
        jdbcTemplate.update(sql, reviewId);
        log.info("Выполнено удаление всех реакций пользователей на отзыв с ID = " + reviewId + ".");
    }
    
    /**
     * Получить "полезность" отзыва, исходя из реакций других пользователей.
     * <p>Положительная реакция: '+1', отрицательная реакция: '-1'.</p>
     *
     * @param reviewId ID отзыва.
     * @return число, показывающее "авторитет" отзыва.
     */
    @Override
    public Integer getUsefulForUserReviewReact(Integer reviewId) {
        int result;
        //Находим положительные реакции на отзыв
//        String sql = "SELECT is_like, COUNT(review_id) FROM review_like_dislike where review_id = ? group by is_like";
        String sqlForPositive = "select count(REVIEW_ID) from REVIEW_LIKE_DISLIKE where IS_LIKE = true and REVIEW_ID = ?";
        Integer positive = jdbcTemplate.queryForObject(sqlForPositive, Integer.class, reviewId);
        String sqlForNegative = "select count(REVIEW_ID) from REVIEW_LIKE_DISLIKE where IS_LIKE = false and REVIEW_ID = ?";
        Integer negative = jdbcTemplate.queryForObject(sqlForNegative, Integer.class, reviewId);
        result = positive - negative;
        log.info("Рассчитан \"авторитет\" отзыва с ID = " + reviewId);
        return result;
    }
    
    private static class UserReviewReactMapper implements RowMapper<UserReviewReact> {
        @Override
        public UserReviewReact mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserReviewReact(rs.getInt("user_id"),
                    rs.getBoolean("is_like"));
        }
    }
}
