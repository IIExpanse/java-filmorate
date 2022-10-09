package ru.yandex.practicum.filmorate.storage.userreview;

import ru.yandex.practicum.filmorate.model.UserReviewReact;

import java.util.List;

public interface UserReviewReactStorage {
    
    /**
     * Создать запись в БД о реакции пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param userLikedReview реакция пользователя.
     * @return UserLikedReview реакция пользователя.
     */
    UserReviewReact addUserReviewReact(UserReviewReact userLikedReview, Integer reviewId);
    
    /**
     * Получить список реакций пользователей на отзыв о фильме.
     *
     * @param reviewId ID отзыва о фильме.
     * @return список реакций (id юзера, isLike).
     */
    List<UserReviewReact> getUserLikeReviewsByReviewId(Integer reviewId);
    
    /**
     * Получить реакцию пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     * @return реакция (id юзера, isLike).
     */
    UserReviewReact getUserReviewReactByReviewIdAndUserId(Integer reviewId, Integer userId);
    
    
    /**
     * Поставить лайк/дизлайк отзыву с reviewId пользователем userId.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     * @param isLike   True - лайк, False - дизлайк.
     */
    void setLikeForReview(Integer reviewId, Integer userId, Boolean isLike);
    
    /**
     * Удалить реакцию пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     */
    void removeUserReviewReactByReviewIdAndUserId(Integer reviewId, Integer userId);
    
    /**
     * Удалить все реакции пользователей на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     */
    void removeUserReviewReactByReviewId(Integer reviewId);
    
    /**
     * Получить "полезность" отзыва, исходя из реакций других пользователей.
     * <p>Положительная реакция: '+1', отрицательная реакция: '-1'.</p>
     *
     * @param reviewId ID отзыва.
     * @return число, показывающее "авторитет" отзыва.
     */
    Integer getUsefulForUserReviewReact(Integer reviewId);
    
    /**
     * Проверить наличие оценки пользователем с ID отзыву с ID.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     */
    Boolean isExistUserReviewReactInDB(Integer reviewId, Integer userId);
}
