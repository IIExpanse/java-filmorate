package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(@Qualifier("ReviewDBService") ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable int id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping
    public ResponseEntity<Collection<Review>> getAllReviews(@RequestParam int filmId,
                                                            @RequestParam @DefaultValue("0") int count) {
        return ResponseEntity.ok(reviewService.getFilmReviewsSortedByUsefulness(filmId, count));
    }

    @PostMapping
    public ResponseEntity<Review> addReview(@Valid @RequestBody Review review) {
        ResponseEntity<Review> response = new ResponseEntity<>(reviewService.addReview(review), HttpStatus.CREATED);
        log.debug("Добавлен новый фильм: {}", response.getBody());

        return response;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLikeToReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.addValueToReview(id, userId, true);
        log.debug("К отзыву с id={} добавлен лайк пользователя с id={}.", id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.addValueToReview(id, userId, false);
        log.debug("К отзыву с id={} добавлен дизлайк пользователя с id={}.", id, userId);
    }

    @PutMapping
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.updateReview(review));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeReview(@PathVariable int id) {
        reviewService.removeReview(id);
        log.debug("Удален отзыв с id={}", id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeValueFromReview(id, userId, true);
        log.debug("У отзыва с id={} удален лайк пользователя с id={}.", id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDisLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeValueFromReview(id, userId, false);
        log.debug("У отзыва с id={} удален дизлайк пользователя с id={}.", id, userId);
    }
}
