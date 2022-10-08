package ru.yandex.practicum.filmorate.storage.userreview.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserLikedReview;
import ru.yandex.practicum.filmorate.storage.userreview.UserLikeReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository("UserLikeReviewDBStorage")
public class UserLikeReviewDBStorage implements UserLikeReviewStorage {
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    public UserLikeReviewDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Создать запись в БД о реакции пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param userLikedReview реакция пользователя.
     * @param reviewId        ID отзыва.
     * @return UserLikedReview реакция пользователя.
     */
    @Override
    public UserLikedReview addUserLikedReview(UserLikedReview userLikedReview, Integer reviewId) {
        removeUserLikeReviewByReviewIdAndUserId(reviewId, userLikedReview.getUserId());
        
        String sqlQuery = "insert into REVIEW_LIKE_DISLIKE " +
                "(REVIEW_ID, USER_ID, IS_LIKE) " +
                "VALUES (?, ?, ?)";
        
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery);
            stmt.setInt(1, reviewId);
            stmt.setInt(2, userLikedReview.getUserId());
            stmt.setBoolean(3, userLikedReview.getIsLike());
            return stmt;
        });
        return userLikedReview;
    }
    
    /**
     * Получить список реакций пользователей на ID отзыв о фильме = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @return список реакций (id юзера, isLike).
     */
    @Override
    public List<UserLikedReview> getUserLikeReviewsByReviewId(Integer reviewId) {
        String sqlQuery = "select * from review_like_dislike where review_id = ?";
        List<UserLikedReview> result = jdbcTemplate.query(sqlQuery, new UserLikedReviewMapper(), reviewId);
        String message = "Получен список реакций пользователей на отзыв с ID = {}.";
        log.info(message, reviewId);
        return result;
    }
    
    /**
     * Получить реакцию пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     * @return реакция (id юзера, isLike).
     */
    @Override
    public UserLikedReview getUserLikeReviewByReviewIdAndUserId(Integer reviewId, Integer userId) {
        String sqlQuery = "select * from REVIEW_LIKE_DISLIKE where REVIEW_ID = ? and USER_ID = ?";
        UserLikedReview result = jdbcTemplate.queryForObject(sqlQuery, new UserLikedReviewMapper(), reviewId, userId);
        String message = "Получена реакция пользователя с ID = {} на отзыв с ID = {}.";
        log.info(message, reviewId, userId);
        return result;
    }
    
    /**
     * Удалить реакцию пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     */
    @Override
    public void removeUserLikeReviewByReviewIdAndUserId(Integer reviewId, Integer userId) {
        String sqlQuery = "delete from REVIEW_LIKE_DISLIKE where REVIEW_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }
    
    /**
     * Удалить реакции пользователей на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     */
    @Override
    public void removeUserLikeReviewByReviewId(Integer reviewId) {
    
    }
    
    private static class UserLikedReviewMapper implements RowMapper<UserLikedReview> {
        @Override
        public UserLikedReview mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserLikedReview(rs.getInt("user_id"),
                    rs.getBoolean("is_like"));
        }
    }
    
}
