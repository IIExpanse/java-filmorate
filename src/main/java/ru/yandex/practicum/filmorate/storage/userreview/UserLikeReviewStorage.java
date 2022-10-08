package ru.yandex.practicum.filmorate.storage.userreview;

import ru.yandex.practicum.filmorate.model.UserLikedReview;

import java.util.List;

public interface UserLikeReviewStorage {
    
    /**
     * Создать запись в БД о реакции пользователя с ID = userId на отзыв с ID = reviewId.
     * @param userLikedReview реакция пользователя.
     * @return UserLikedReview реакция пользователя.
     */
    UserLikedReview addUserLikedReview(UserLikedReview userLikedReview, Integer reviewId);
    
    /**
     * Получить список реакций пользователей на отзыв о фильме.
     *
     * @param reviewId ID отзыва о фильме.
     * @return список реакций (id юзера, isLike).
     */
    List<UserLikedReview> getUserLikeReviewsByReviewId(Integer reviewId);
    
    /**
     * Получить реакцию пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     * @return реакция (id юзера, isLike).
     */
    UserLikedReview getUserLikeReviewByReviewIdAndUserId(Integer reviewId, Integer userId);
    
    
    /**
     * Удалить реакцию пользователя с ID = userId на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     */
    void removeUserLikeReviewByReviewIdAndUserId(Integer reviewId, Integer userId);

    /**
     * Удалить реакции пользователей на отзыв с ID = reviewId.
     *
     * @param reviewId ID отзыва о фильме.
     * @param userId   ID пользователя.
     */
    void removeUserLikeReviewByReviewId(Integer reviewId);

}
