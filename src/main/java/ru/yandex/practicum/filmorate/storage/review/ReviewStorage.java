package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserLikedReview;

import java.util.List;

/**
 * Данный интерфейс предполагает работу с двумя источниками данных:
 * <p>1. таблица отзывов;</p>
 * <p>2. таблица лайков-дизлайков пользователей фильмам.</p>
 */
public interface ReviewStorage {
    
    /**
     * Добавление нового отзыва.
     *
     * @param review добавляемый отзыв.
     */
    Review addReview(Review review);
    
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
    
    void removeLikeForReview(Integer reviewId, Integer userId);
    
    void removeDisLikeForReview(Integer reviewId, Integer userId);
    
    /**
     * Получить список отзывов о фильмах пользователя с ID.
     *
     * @param userId ID пользователя.
     */
    List<Review> getReviewsByUserId(Integer userId);
    
    /**
     * Обновление информации о существующем отзыве о фильме.
     *
     * @param review обновляемый отзыв.
     * @return обновлённый фильм.
     */
    public Review updateInStorage(Review review);
    
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
     * @param isLike   True - лайк, False - дизлайк.
     */
    void setLikeForReview(Integer reviewId, Integer userId, Boolean isLike);
    
    /**
     * Проверить наличие отзыва по его reviewId.
     *
     * @param reviewId ID отзыва.
     */
    boolean existReviewById(Integer reviewId);
    
    /**
     * Метод определения количества пользователей, отреагировавших на отзыв о фильме с ID = reviewId.
     *
     * @param userLikedReviews ID отзыва о фильме.
     * @return "полезность" отзыва, судя по реакциям пользователей на него.
     */
    Integer getUsefulnessOfTheReview(List<UserLikedReview> userLikedReviews);
    
    /**
     * Получить популярные отзывы из БД в количестве count штук.
     * @param count количество.
     */
    List<Review> getPopularReviewsWithCount(Integer count);
    
    /**
     * Получить популярные отзывы из БД в количестве count штук.
     * @param filmId,
     * @param count количество.
     * @return список отзывов.
     */
    List<Review> getPopularReviewsWithCountAndFilmId(Integer filmId, Integer count);
}
