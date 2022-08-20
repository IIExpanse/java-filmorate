package ru.yandex.practicum.filmorate.exception.like;

public class LikeAlreadyAddedException extends RuntimeException {
    public LikeAlreadyAddedException(String message) {
        super(message);
    }
}
