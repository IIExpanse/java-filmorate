package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.userreviewreact.UserReviewReactNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.validation.ValidationService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.userreview.UserReviewReactStorage;

import java.util.List;

@Slf4j
@Service("ReviewDBService")
public class ReviewDBService implements ReviewService {
    
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final UserReviewReactStorage userReviewReactStorage;
    private final ValidationService validationService;
    
    @Autowired
    public ReviewDBService(@Qualifier("ReviewDBStorage") ReviewStorage reviewStorage,
                           @Qualifier("UserDBService") UserService userService,
                           @Qualifier("FilmDBService") FilmService filmService,
                           @Qualifier("UserLikeReviewDBStorage") UserReviewReactStorage userReviewReactStorage,
                           ValidationService validationService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.userReviewReactStorage = userReviewReactStorage;
        this.validationService = validationService;
    }
    
    /**
     * Получить все отзывы.
     */
    @Override
    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }
    
    /**
     * Добавление нового отзыва.
     *
     * @param review добавляемый отзыв.
     */
    @Override
    public Review addReview(Review review) {
        validationService.checkReview(review);
        isExistFilmInDBByReview(review);
        isExistUserInDB(review);
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
        validationService.checkReview(review);
        isExistFilmInDBByReview(review);
        isExistUserInDB(review);
        return reviewStorage.updateInStorage(review);
    }
    
    
    /**
     * Удалить отзыв о фильме с reviewId.
     *
     * @param reviewId ID отзыва.
     */
    @Override
    public void removeReviewById(Integer reviewId) {
        reviewStorage.removeReviewById(reviewId);
        //удаление всех реакций пользователей на отзыв
        userReviewReactStorage.removeUserReviewReactByReviewId(reviewId);
        log.info("Выполнено удаление отзыва из БД отзывов и его оценок из БД реакций на отзывы.");
    }
    
    /**
     * Получить отзыв по review_id
     *
     * @param reviewId ID отзыва.
     */
    @Override
    public Review getReviewById(Integer reviewId) {
        log.info("Запрос отзыва по ID.");
        Review review = reviewStorage.getReviewById(reviewId);
        if (review == null) {
            String error = "Error 404. В БД нет отзыва с ID = " + reviewId + ".";
            throw new ReviewNotFoundException(error);
        }
        //Присваиваем 'авторитет' отзыва из БД оценок отзывов.
        review.setUseful(userReviewReactStorage.getUsefulForUserReviewReact(reviewId));
        return review;
    }
    
    /**
     * Получение всех отзывов по идентификатору фильма, если фильм не указан, то все.
     * Если кол-во не указано, то 10.
     *
     * @param filmId ID фильма.
     * @param count  количество отзывов в отчёте.
     */
    // TODO: 2022.10.08 20:52:08 НАДО БУДЕТ ДОДЕЛАТЬ ЛОГИКУ РАБОТЫ!!! - @Dmitriy_Gaju
    @Override
    public List<Review> getReviewsByFilmIdAndWithCount(Integer filmId, Integer count) {
        if (filmId == null) {
            reviewStorage.getPopularReviewsWithCount(count);
        }
        return reviewStorage.getPopularReviewsWithCountAndFilmId(filmId, count);
    }
    
    /**
     * Получить список отзывов по ID фильма.
     * ////////////////////////////////////////////Не нужный метод./////////////////////////////////////
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
     * ////////////////////////////////////////////Не нужный метод./////////////////////////////////////
     *
     * @param userId ID пользователя.
     */
    @Override
    public List<Review> getReviewsByUserId(Integer userId) {
        return reviewStorage.getReviewsByUserId(userId);
    }
    
    /**
     * Удаление пользователем лайка/дизлайка отзыву.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     */
    @Override
    public void removeReactForReview(Integer reviewId, Integer userId) {
        isExistUserReviewReactInDB(reviewId, userId);
        userReviewReactStorage.removeUserReviewReactByReviewIdAndUserId(reviewId, userId);
        Review review = reviewStorage.getReviewById(reviewId);
        //Пересчитываем авторитет отзыва.
        Integer useful = userReviewReactStorage.getUsefulForUserReviewReact(reviewId);
        review.setUseful(useful);
        reviewStorage.updateInStorage(review);
        log.debug("Выполнено удаление лайка/дизлайка отзыву (ID = {}) пользователем (ID = {})", reviewId, userId);
    }
    
    
    /**
     * Поставить лайк/дизлайк отзыву с reviewId пользователем с userId.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     * @param isLike   True - лайк, False - дизлайк.
     */
    @Override
    public void setReactForReview(Integer reviewId, Integer userId, Boolean isLike) {
        
        Review review = reviewStorage.getReviewById(reviewId);
        if (review == null) {
            String error = String.format("Error 404. При попытке поставить лайк/дизлайк " +
                    "отзыву (ID = %d) пользователем (ID = %d) не найден отзыв в БД.", reviewId, userId);
            log.info(error);
            throw new ReviewNotFoundException(error);
        }
        isExistReviewById(reviewId);
        if (isLike == null) {
            String error = "Error 400. Ошибка при добавлении лайка отзыву. Переданный параметр 'isLike' = null.";
            log.info(error);
            throw new RuntimeException(error);
        }
        
        userReviewReactStorage.setLikeForReview(reviewId, userId, isLike);
        Integer useful = userReviewReactStorage.getUsefulForUserReviewReact(reviewId);
        review.setUseful(useful);
        reviewStorage.updateInStorage(review);
    }
    
    /**
     * Проверить наличие отзыва по его reviewId.
     *
     * @param reviewId – ID отзыва.
     * @throws ReviewNotFoundException исключение.
     */
    private void isExistReviewById(Integer reviewId) {
        if (!reviewStorage.existReviewById(reviewId)) {
            String error = "Error 404. Отзыв с ID = '" + reviewId + "' не найден в БД.";
            log.info(error);
            throw new ReviewNotFoundException(error);
        }
    }
    
    /**
     * Проверка наличия пользователя в БД по отзыву.
     *
     * @param review отзыв.
     * @throws UserNotFoundException исключение.
     */
    private void isExistUserInDB(Review review) {
        Integer userId = review.getUserId();
        User user = userService.getUser(userId);
        if (user == null) {
            String error = "В БД нет пользователя с ID = " + userId + ".";
            log.info(error);
            throw new UserNotFoundException(error);
        }
    }
    
    /**
     * Проверка наличия фильма в БД по отзыву.
     *
     * @param review отзыв.
     */
    private void isExistFilmInDBByReview(Review review) {
        Film film = filmService.getFilm(review.getFilmId());
        if (film == null) {
            String error = "В БД нет фильма с ID = " + review.getReviewId() + ".";
            log.info(error);
            throw new FilmNotFoundException(error);
        }
    }
    
    /**
     * Проверка наличия или отсутствия реакции пользователя на отзыв.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID пользователя.
     * @throws UserReviewReactNotFoundException исключение о не найденной записи.
     */
    private void isExistUserReviewReactInDB(Integer reviewId, Integer userId) {
        if (!userReviewReactStorage.isExistUserReviewReactInDB(reviewId, userId)) {
            String error = String.format("Error 404. Не найдена запись об оценке пользователем (ID = %d) " +
                    "отзыва (ID = %d)", userId, reviewId);
            log.info(error);
            throw new UserReviewReactNotFoundException(error);
        }
    }
    
}
