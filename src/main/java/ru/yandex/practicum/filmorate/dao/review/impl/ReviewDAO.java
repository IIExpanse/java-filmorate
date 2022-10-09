package ru.yandex.practicum.filmorate.dao.review.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository("ReviewDAO")
@Primary
@AllArgsConstructor
public class ReviewDAO implements ru.yandex.practicum.filmorate.dao.review.ReviewDAO {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review getReviewById(int reviewId) {
        String sqlQuery = "SELECT * FROM \"reviews\" WHERE review_id = ?";
        Review review = jdbcTemplate.queryForObject(sqlQuery, new ReviewMapper(), reviewId);
        if (review == null) {

            throw new ReviewNotFoundException(
                    String.format("Ошибка при получении: отзыв с id=%d не найден.", reviewId));
        }
        return review;
    }

    @Override
    public Collection<Review> getAllReviews() {
        String sql = "SELECT * FROM \"reviews\"";
        return jdbcTemplate.query(sql, new ReviewMapper());
    }

    @Override
    public Collection<Review> getReviewsByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM \"reviews\" WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, new ReviewMapper(), filmId);
    }

    @Override
    public Review addReview(Review review) {
        Number num;

        String sqlQuery = "INSERT INTO \"reviews\" " +
                "(FILM_ID, USER_ID, IS_POSITIVE, CONTENT) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlQuery, new String[]{"review_id"});
            ps.setInt(1, review.getFilmId());
            ps.setInt(2, review.getUserId());
            ps.setBoolean(3, review.isPositive());
            ps.setString(4, review.getContent());
            return ps;
        }, keyHolder);

        num = keyHolder.getKey();
        if (num != null) {
            review.setReviewId(keyHolder.getKey().intValue());

        } else throw new RuntimeException("Ошибка: отзыв не был добавлен.");

        return review;
    }

    @Override
    public void addValueToReview(int reviewId, int userId, boolean isLike) {
        try {
            String sql = "INSERT INTO REVIEW_LIKE_DISLIKE (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, reviewId, userId, isLike);

        } catch (DuplicateKeyException e) {
            throw new ReviewNotFoundException(
                    "Ошибка при добавлении лайка/дизлайка: лайк/дизлайк уже существует.");

        } catch (DataIntegrityViolationException e) {
            throw new ReviewNotFoundException(
                    "Ошибка при добавлении лайка/дизлайка: отзыв и/или пользователь не найден.");

        }
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery;
        int id = review.getReviewId();

        try {
            sqlQuery = "SELECT REVIEW_ID FROM \"reviews\" WHERE REVIEW_ID = " + id;
            jdbcTemplate.queryForObject(sqlQuery, Integer.class);

        } catch (DataAccessException e) {
            throw new ReviewNotFoundException(String.format("Ошибка при обновлении: отзыв с id=%d не найден.", id));
        }

        sqlQuery = "UPDATE \"reviews\" " +
                "SET IS_POSITIVE = ?, CONTENT = ? WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery,
                review.isPositive(),
                review.getContent(),
                review.getReviewId()
        );
        return review;
    }

    @Override
    public void removeReview(int reviewId) {
        String sql = "DELETE FROM \"reviews\" WHERE REVIEW_ID = ?";
        int affected = jdbcTemplate.update(sql, reviewId);

        if (affected == 0) {
            throw new ReviewNotFoundException(String.format("Ошибка: отзыв с id=%d не найден.", reviewId));
        }
    }

    @Override
    public void removeValueFromReview(int reviewId, int userId, boolean isLike) {
        String sql = "DELETE FROM REVIEW_LIKE_DISLIKE WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = ?";
        int affected = jdbcTemplate.update(sql, reviewId, userId, isLike);

        if (affected == 0) {
            throw new ReviewNotFoundException(String.format("Ошибка: лайк к отзыву с id=%d не найден.", reviewId));
        }
    }

    private class ReviewMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Integer usefulness;
            Review review = new Review(rs.getInt("review_id"),
                    rs.getString("content"),
                    rs.getBoolean("is_positive"),
                    rs.getInt("film_id"),
                    rs.getInt("user_id"),
                    0);
            int reviewId = review.getReviewId();

            String sqlQuery = "SELECT " +
                    "(SELECT COUNT(REVIEW_ID) FROM REVIEW_LIKE_DISLIKE WHERE IS_LIKE = true AND REVIEW_ID = ?)" +
                    " - (SELECT COUNT(REVIEW_ID) FROM REVIEW_LIKE_DISLIKE WHERE IS_LIKE = false AND REVIEW_ID = ?)";
            usefulness = jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId, reviewId);

            if (usefulness != null) {
                review.setUseful(usefulness);

            } else throw new RuntimeException("Ошибка при расчете рейтинга отзыва.");

            return review;
        }
    }
}
