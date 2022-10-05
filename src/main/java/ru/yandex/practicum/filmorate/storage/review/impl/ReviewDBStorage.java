package ru.yandex.practicum.filmorate.storage.review.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class ReviewDBStorage implements ReviewStorage {
    
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    public ReviewDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Получить отзыв по review_id
     *
     * @param reviewId ID отзыва.
     */
    @Override
    public Review getReviewById(Integer reviewId) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id = ?";
        log.debug("Запрошен отзыв с ID = {}", reviewId);
        Review result = jdbcTemplate.queryForObject(sqlQuery, new ReviewMapper(), reviewId);
        log.debug("Получен отзыв с ID = {}\t—\t{}", reviewId, result);
        return result;
    }
    
    /**
     * Получить список отзывов по ID фильма.
     *
     * @param filmId ID фильма.
     */
    @Override
    public List<Review> getReviewsByFilmId(Integer filmId) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE FILM_ID = ?";
        log.debug("Запрошен список отзывов о фильме с ID = {}", filmId);
        List<Review> result = jdbcTemplate.query(sqlQuery, new ReviewMapper(), filmId);
        log.debug("Получен список отзывов фильма с ID = {}\t—\t{}", filmId, result);
        return result;
    }
    
    /**
     * Получить список отзывов о фильмах пользователя с ID.
     *
     * @param userId ID пользователя.
     */
    @Override
    public List<Review> getReviewsByUserId(Integer userId) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE USER_ID = ?";
        log.debug("Запрошен список отзывов о фильмах пользователя с ID = {}", userId);
        List<Review> result = jdbcTemplate.query(sqlQuery, new ReviewMapper(), userId);
        log.debug("Получен список отзывов о фильмах пользователя с ID = {}\t—\t{}", userId, result);
        return result;
    }
    
    /**
     * Удалить отзыв о фильме с reviewId.
     *
     * @param reviewId ID отзыва.
     */
    @Override
    public void removeReviewById(Integer reviewId) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        log.debug("Запрос на удаление отзыва о фильме с reviewId = {}.", reviewId);
        jdbcTemplate.update(sqlQuery, reviewId);
        // TODO: 2022.10.06 00:49:05 Удалить лайки-дизлайки отзыва. - @Dmitriy_Gaju
        log.debug("Выполнен запрос на удаление отзыва о фильме с reviewId = {}.", reviewId);
        
    }
    
    /**
     * Поставить лайк/дизлайк отзыву с reviewId пользователем userId.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     * @param isLike True - лайк, False - дизлайк.
     */
    @Override
    public void setLikeForReview(Integer reviewId, Integer userId, Boolean isLike) {
        String sqlQueryForDelete = "DELETE FROM REVIEW_LIKE_DISLIKE WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQueryForDelete, reviewId, userId);
        String sqlQueryForAddLike = "MERGE INTO REVIEW_LIKE_DISLIKE (review_id, user_id, is_like)" +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQueryForAddLike, reviewId, userId, isLike);
    }
    
    private static class ReviewMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Review(rs.getInt("review_id"), rs.getString("content"),
                    rs.getBoolean("is_positive"), rs.getInt("film_id"),
                    rs.getInt("user_id"));
        }
    }
    
}
