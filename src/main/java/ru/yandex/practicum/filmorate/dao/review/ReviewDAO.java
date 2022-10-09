package ru.yandex.practicum.filmorate.dao.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewDAO {

    Review getReviewById(int reviewId);

    Collection<Review> getAllReviews();

    Collection<Review> getReviewsByFilmId(int filmId);

    Review addReview(Review review);

    void addValueToReview(int reviewId, int userId, boolean isLike);

    Review updateReview(Review review);

    void removeReview(int reviewId);

    void removeValueFromReview(int reviewId, int userId, boolean isLike);
}
