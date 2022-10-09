package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.review.ReviewService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final FilmService filmService;
    private final UserService userService;
    private final ReviewService reviewService;
    
    @Autowired
    public ReviewController(@Qualifier("FilmDBService") FilmService filmService,
                            @Qualifier("UserDBService") UserService userService,
                            @Qualifier("ReviewDBService") ReviewService reviewService) {
        this.filmService = filmService;
        this.userService = userService;
        this.reviewService = reviewService;
    }
    
    /**
     * Получить все отзывы.
     */
    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
    
    /**
     * Добавление нового отзыва.
     *
     * @param review добавляемый отзыв.
     */
    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }
    
    /**
     * Редактирование имеющегося отзыва.
     */
    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateInStorage(review);
    }
    
    /**
     * DELETE /reviews/{id}
     * Удалить отзыв из БД.
     */
    @DeleteMapping("/{id}")
    public void removeReview(@PathVariable Integer id) {
        reviewService.removeReviewById(id);
    }
    
    /**
     * `GET /reviews/{id}`
     * Получение отзыва по идентификатору.
     */
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }
    
    /**
     * GET /reviews?filmId={filmId}&count={count}
     * Получение всех отзывов по идентификатору фильма, если фильм не указан, то все.
     * Если кол-во не указано, то 10.
     */
    @GetMapping("?filmId")
    public List<Review> getReviewsByFilmIdAnWithCount(@RequestParam(required = false) Integer filmId,
                                                      @RequestParam(required = false,
                                                              defaultValue = "10") Integer count) {
//        List<Review> reviews = reviewService.ge;
        return null;
    }
    
    /**
     * PUT /reviews/{id}/like/{userId}  — пользователь ставит лайк отзыву.
     */
    @PutMapping("{id}/dislike/{userId}")
    public void setDisLikeForReview(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.setReactForReview(id, userId, false);
    }
    
    /**
     * PUT /reviews/{id}/like/{userId}  —
     * <p>пользователь ставит лайк отзыву.</p>
     */
    @PutMapping("{id}/like/{userId}")
    public void setLikeForReview(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.setReactForReview(id, userId, true);
    }
    
    /**
     * - `DELETE /reviews/{id}/like/{userId}`  — пользователь удаляет лайк/дизлайк отзыву.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.removeReactForReview(id, userId);
    }
    
    /**
     * - `DELETE /reviews/{id}/dislike/{userId}`  — пользователь удаляет дизлайк отзыву.
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDisLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.removeReactForReview(id, userId);
    }
    
    
    /**
     * Посмотреть все отзывы пользователя.
     * <p>Этот метод лишний.</p>
     *
     * @param userId ID пользователя.
     * @return отзывы пользователя о разных фильмах
     */
    public ResponseEntity<?> getReviewsByUserId(Integer userId) {
        return null;
    }
    
}
