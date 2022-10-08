package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.userreview.UserLikeReviewStorage;

import java.util.List;

@Slf4j
@Service("ReviewDBService")
public class ReviewDBService implements ReviewService {
    
    ReviewStorage reviewStorage;
    UserService userService;
    FilmService filmService;
    UserLikeReviewStorage userLikedReviewStorage;
    
    @Autowired
    public ReviewDBService(@Qualifier("ReviewDBStorage") ReviewStorage reviewStorage,
                           @Qualifier("UserDBService") UserService userService,
                           @Qualifier("FilmDBService") FilmService filmService,
                           @Qualifier("UserLikeReviewDBStorage") UserLikeReviewStorage userLikedReviewStorage) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.userLikedReviewStorage = userLikedReviewStorage;
    }
    
    /**
     * Добавление нового отзыва.
     *
     * @param review добавляемый отзыв.
     */
    @Override
    public Review addReview(Review review) {
        
        Film film = filmService.getFilm(review.getFilmId());
        if (film == null) {
            String error = "В БД нет фильма с ID = " + review.getId() + ".";
            log.info(error);
            throw new FilmNotFoundException(error);
        }
        User user = userService.getUser(review.getUserId());
        if (user == null) {
            String error = "В БД нет пользователя с ID = " + review.getId() + ".";
            log.info(error);
            throw new UserNotFoundException(error);
        }
        //Добавить в БД отзывов. И всё!
        return reviewStorage.addReview(review);
    }
    
    /**
     * Обновление информации о существующем отзыве о фильме.
     *
     * @param review обновляемый отзыв.
     * @return обновлённый отзыв.
     */
    @Override
    public Review updateInStorage(Review review) {
        Film film = filmService.getFilm(review.getFilmId());
        if (film == null) {
            String error = "В БД нет фильма с ID = " + review.getId() + ".";
            log.info(error);
            throw new FilmNotFoundException(error);
        }
        User user = userService.getUser(review.getUserId());
        if (user == null) {
            String error = "В БД нет пользователя с ID = " + review.getId() + ".";
            log.info(error);
            throw new UserNotFoundException(error);
        }
        return reviewStorage.updateInStorage(review);
    }
    
    
    /**
     * Получить отзыв по review_id
     *
     * @param reviewId ID отзыва.
     */
    @Override
    public Review getReviewById(Integer reviewId) {
    
        Review review = reviewStorage.getReviewById(reviewId);
        if (review == null) {
            String error = "Error 404. В БД нет отзыва с ID = " + reviewId + ".";
            throw new ReviewNotFoundException(error);
        }
        return review;
    }
    
    /**
     * Получение всех отзывов по идентификатору фильма, если фильм не указан, то все.
     * Если кол-во не указано, то 10.
     * @param filmId ID фильма.
     * @param count количество отзывов в отчёте.
     */
    @Override
    public List<Review> getReviewsByFilmIdAnWithCount(Integer filmId, Integer count) {
        if (filmId == null) {
            reviewStorage.getPopularReviewsWithCount(count);
        }
        return reviewStorage.getPopularReviewsWithCountAndFilmId(filmId, count);
    }
    
    
    /**
     * Получить список отзывов по ID фильма.
     *
     * @param filmId ID фильма.
     */
    @Override
    public List<Review> getReviewsByFilmId(Integer filmId) {
        List<Review> reviewsByFilmId = reviewStorage.getReviewsByFilmId(filmId);
        log.info("TODO: 2022.10.07 23:52:57 Сделать сортировку по \"полезности\" отзывов. - @Dmitriy_Gaju");
        // TODO: 2022.10.07 23:52:57 Сделать сортировку по "полезности" отзывов. - @Dmitriy_Gaju
        return reviewsByFilmId;
    }
    
    /**
     * Получить список отзывов о фильмах пользователя с ID.
     *
     * @param userId ID пользователя.
     */
    @Override
    public List<Review> getReviewsByUserId(Integer userId) {
        return reviewStorage.getReviewsByUserId(userId);
    }
    
    /**
     * Удалить отзыв о фильме с reviewId.
     *
     * @param reviewId ID отзыва.
     */
    @Override
    public void removeReviewById(Integer reviewId) {
        reviewStorage.removeReviewById(reviewId);
        userLikedReviewStorage.removeUserLikeReviewByReviewId(reviewId);
    }
    
    /**
     * Удалить лайк отзыву.
     */
    @Override
    public void removeLikeForReview(Integer reviewId, Integer userId) {
        userLikedReviewStorage.removeUserLikeReviewByReviewIdAndUserId(reviewId, userId);
    }
    
    /**
     * Удалить дизлайк отзыву.
     */
    @Override
    public void removeDisLikeForReview(Integer reviewId, Integer userId) {
        userLikedReviewStorage.removeUserLikeReviewByReviewIdAndUserId(reviewId, userId);
        
        
        /**
         * Поставить лайк/дизлайк отзыву с reviewId пользователем userId.
         *
         * @param reviewId ID отзыва.
         * @param userId   ID пользователя.
         * @param isLike   True - лайк, False - дизлайк.
         */
    @Override
    public void setLikeForReview(Integer reviewId, Integer userId, Boolean isLike) {
        if (!reviewStorage.existReviewById(reviewId)) {
            String error = "Error 404. Ошибка при добавлении лайка отзыву. Отзыв с ID = '" + reviewId + "' не найден в БД.";
            log.info(error);
            throw new ReviewNotFoundException(error);
        }
        
        if (userService.getUser(userId) == null) {
            String error = String.format("Error 404. Ошибка при добавлении лайка отзыву. " +
                    "Пользователь с ID=%d не найден в БД.", userId);
            log.info(error);
            throw new UserNotFoundException(error);
        }
        
        if (isLike == null) {
            String error = "Error 400. Ошибка при добавлении лайка отзыву. Переданный параметр 'isLike' = null.";
            log.info(error);
            throw new RuntimeException(error);
        }
        
        reviewStorage.setLikeForReview(reviewId, userId, isLike);
    }
    
}
