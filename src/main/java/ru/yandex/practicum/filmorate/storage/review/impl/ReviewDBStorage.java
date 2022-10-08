package ru.yandex.practicum.filmorate.storage.review.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserLikedReview;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.userreview.UserLikeReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository("ReviewDBStorage")
public class ReviewDBStorage implements ReviewStorage {
    
    JdbcTemplate jdbcTemplate;
    UserLikeReviewStorage userLikedReviewStorage;
    
    @Autowired
    public ReviewDBStorage(JdbcTemplate jdbcTemplate,
                           @Qualifier("UserLikeReviewDBStorage") UserLikeReviewStorage userLikedReviewStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userLikedReviewStorage = userLikedReviewStorage;
    }
    
    /**
     * Добавление нового отзыва.
     *
     * @param review добавляемый отзыв.
     */
    @Override
    public Review addReview(Review review) {
        String sqlQuery = "insert into REVIEWS " +
                "(FILM_ID, USER_ID, IS_POSITIVE, CONTENT) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"reviews_id"});
            stmt.setInt(1, review.getFilmId());
            stmt.setInt(2, review.getUserId());
            stmt.setBoolean(3, review.getIsPositive());
            stmt.setString(4, review.getContent());
            return stmt;
        }, keyHolder);
        //отзыву, записанному в БД, присваиваем ID из БД.
        review.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        
        return review;
    }
    
    /**
     * Обновление информации о существующем отзыве о фильме.
     *
     * @param review обновляемый отзыв.
     * @return обновляемый отзыв.
     */
    @Override
    public Review updateInStorage(Review review) {
        String sqlQuery = "update REVIEWS " +
                "SET REVIEW_ID = ?, FILM_ID = ?, USER_ID = ?, IS_POSITIVE = ?, CONTENT = ? WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery,
                review.getId(),
                review.getFilmId(),
                review.getUserId(),
                review.getIsPositive(),
                review.getContent(),
                review.getId()
        );
        return review;
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
        Review review = jdbcTemplate.queryForObject(sqlQuery, new ReviewMapper(), reviewId);
        if (review == null) {
            String error = "Error 404. Ошибка при получении отзыва из БД по ID отзыва = '" + reviewId + "'.";
            log.error(error);
            throw new ReviewNotFoundException(error);
        }
        //Считываем лайки пользователей этому отзыву.
        List<UserLikedReview> userLikeReviewDBStorages =
                userLikedReviewStorage.getUserLikeReviewsByReviewId(reviewId);
        //Рассчитываем "полезность" отзыва и присваиваем отзыву.
        review.setUseful(getUsefulnessOfTheReview(userLikeReviewDBStorages));
        log.debug("Получен отзыв с ID = {}\t—\t{}", reviewId, review);
        return review;
    }
    
    /**
     * Получить популярные отзывы из БД в количестве count штук.
     *
     * @param count количество.
     */
    @Override
    public List<Review> getPopularReviewsWithCount(Integer count) {
    
        String sql = "select R.*, NVL(SUM(VALUE_LIKE), 0) AS VALUE_LIKE FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKE_DISLIKE AS RLD ON R.REVIEW_ID = RLD.REVIEW_ID " +
                "left join like_value as LV ON RLD.IS_LIKE = LV.is_like " +
                "group by R.REVIEW_ID order by COUNT(RLD.USER_ID) desc limit ?";
        
        
//                String sql = "select R.* FROM REVIEWS AS R " +
//                "LEFT JOIN REVIEW_LIKE_DISLIKE AS RLD ON R.REVIEW_ID = RLD.REVIEW_ID " +
//                " group by R.FILM_ID order by COUNT(RLD.user_id) desc limit ?";
    
        return jdbcTemplate.query(sql, new ReviewMapper(), count);
    }
    
    /**
     * Получить популярные отзывы из БД в количестве count штук.
     *
     * @param filmId ID фильма.
     * @param count  количество.
     * @return список отзывов.
     */
    @Override
    public List<Review> getPopularReviewsWithCountAndFilmId(Integer filmId, Integer count) {
        String sql =  "SELECT R.*, NVL(SUM(value_like), 0) AS value_like " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKE_DISLIKE AS R ON R.REVIEW_ID = R.REVIEW_ID " +
                "LEFT JOIN REVIEW_LIKE_DISLIKE AS RLD ON R.REVIEW_ID = RLD.REVIEW_ID " +
                "WHERE FILM_ID = ? GROUP BY R.REVIEW_ID ORDER BY value_like DESC LIMIT ?";
        
        String sqlQuery =  "SELECT R.*, NVL(SUM(VALUE_LIKE), 0) AS MARK " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKE_DISLIKE AS RLD ON R.REVIEW_ID = RLD.REVIEW_ID " +
                "LEFT JOIN LIKE_VALUE AS  LV ON RLD.REVIEW_ID = LV.VALUE_LIKE " +
                "WHERE FILM_ID = ? GROUP BY R.REVIEW_ID ORDER BY MARK DESC LIMIT ?";
        jdbcTemplate.query(sqlQuery, new ReviewMapper(), filmId, count);
        jdbcTemplate.query(sql, new ReviewMapper(), filmId, count);
        return null;
    }
    
    /**
     * Получить список отзывов по ID фильма.
     *
     * @param filmId ID фильма.
     */
    @Override
    public List<Review> getReviewsByFilmId(Integer filmId) {
        String sqlQuery = "SELECT * FROM reviews WHERE film_id = ?";
        log.debug("Запрошен список отзывов о фильме с ID = {}", filmId);
        List<Review> result = jdbcTemplate.query(sqlQuery, new ReviewMapper(), filmId);
        //Проходим по циклу и присваиваем каждому отзыву его "правильность".
        for (Review r : result) {
            Integer reviewId = r.getId();
            List<UserLikedReview> userLikedReviews = userLikedReviewStorage.getUserLikeReviewsByReviewId(reviewId);
            r.setUseful(getUsefulnessOfTheReview(userLikedReviews));
        }
        log.debug("Получен список отзывов фильма с ID = {}\t—\t{}", filmId, result);
        return result;
    }
    
    /**
     * Удалить лайк отзыву.
     */
    @Override
    public void removeLikeForReview(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM REVIEW_LIKE_DISLIKE " +
                "WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }
    
    
    /**
     * Удалить дизлайк отзыву.
     */
    @Override
    public void removeDisLikeForReview(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM REVIEW_LIKE_DISLIKE " +
                "WHERE REVIEW_ID = ? AND USER_ID = ?";
                jdbcTemplate.update(sql);
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
     * Проверить наличие отзыва по его reviewId.
     *
     * @param reviewId ID отзыва.
     */
    public boolean existReviewById(Integer reviewId) {
        log.debug("Вызван метод проверки наличия в БД отзыва с ID = {}.", reviewId);
        String sqlQuery = "select COUNT(*) from REVIEWS where REVIEW_ID = ?";
        Integer result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId);
        log.debug("Количество записей в БД отзыва с ID = {} равно '{}'.", result, reviewId);
        return result != null && result.equals(1);
    }
    
    private static class ReviewMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Review(rs.getString("content"),
                    rs.getBoolean("is_positive"),
                    rs.getInt("film_id"),
                    rs.getInt("user_id"));
        }
    }
    
    /**
     * Метод определения количества пользователей, отреагировавших на отзыв о фильме с ID = reviewId.
     *
     * @param userLikedReviews список отзывов о фильме.
     * @return "полезность" отзыва, судя по реакциям пользователей на него.
     */
    @Override
    public Integer getUsefulnessOfTheReview(List<UserLikedReview> userLikedReviews) {
        Integer count = 0;
        for (UserLikedReview uLR : userLikedReviews) {
            if (uLR.getIsLike()) {
                count++;
            } else {
                count--;
            }
        }
        return count;
    }
    
}
