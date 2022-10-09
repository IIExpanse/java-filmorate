package ru.yandex.practicum.filmorate.exception.review;

public class ReviewObjectNotFoundException extends RuntimeException {
    public ReviewObjectNotFoundException(String message) {
        super(message);
    }
}
