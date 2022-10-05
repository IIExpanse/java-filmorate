package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

/**
 * Данный интерфейс предполагает работу с двумя источниками данных:
 * <p>1. таблица отзывов;</p>
 * <p>2. таблица лайков-дизлайков пользователей фильмам.</p>
 */
public interface ReviewStorage {
    /**
     * Получить отзыв по review_id
     *
     * @param reviewId ID отзыва.
     */
    Review getReviewById(Integer reviewId);
    
    /**
     * Получить список отзывов по ID фильма.
     *
     * @param filmId ID фильма.
     */
    List<Review> getReviewsByFilmId(Integer filmId);
    
    /**
     * Получить список отзывов о фильмах пользователя с ID.
     *
     * @param userId ID пользователя.
     */
    List<Review> getReviewsByUserId(Integer userId);
    
    /**
     * Удалить отзыв о фильме с reviewId.
     *
     * @param reviewId ID отзыва.
     */
    void removeReviewById(Integer reviewId);
    
    /**
     * Поставить лайк/дизлайк отзыву с reviewId пользователем userId.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     * @param isLike True - лайк, False - дизлайк.
     */
    void setLikeForReview(Integer reviewId, Integer userId, Boolean isLike);
}
