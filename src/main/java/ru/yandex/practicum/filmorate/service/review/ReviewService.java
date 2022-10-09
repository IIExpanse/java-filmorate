package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    
    /**
     * Получить все отзывы.
     */
    List<Review> getAllReviews();
    
    /**
     * Добавление нового отзыва.
     *
     * @param review добавляемый отзыв.
     */
    Review addReview(Review review);
    
    /**
     * Обновление информации о существующем отзыве о фильме.
     *
     * @param review обновляемый отзыв.
     * @return обновлённый отзыв.
     */
    Review updateInStorage(Review review);
    
    /**
     * Получить отзыв по review_id
     *
     * @param reviewId ID отзыва.
     */
    Review getReviewById(Integer reviewId);
    
    /**
     * Получение всех отзывов по идентификатору фильма, если фильм не указан, то все.
     * Если кол-во не указано, то 10.
     *
     * @param filmId ID фильма.
     * @param count  количество отзывов в отчёте.
     */
    List<Review> getReviewsByFilmIdAndWithCount(Integer filmId, Integer count);
    
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
     * Удаление пользователем лайка/дизлайка отзыву.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     */
    void removeReactForReview(Integer reviewId, Integer userId);
    
    /**
     * Поставить лайк/дизлайк отзыву с reviewId пользователем userId.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     * @param isLike   True - лайк, False - дизлайк.
     */
    void setReactForReview(Integer reviewId, Integer userId, Boolean isLike);
    
    //void removeDisLikeForReview(Integer reviewId, Integer userId);
}
