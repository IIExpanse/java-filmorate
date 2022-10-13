package ru.yandex.practicum.filmorate.exception.review;

public class ReviewAlreadyLikedException extends RuntimeException {
    public ReviewAlreadyLikedException(String message) {
        super(message);
    }
}
