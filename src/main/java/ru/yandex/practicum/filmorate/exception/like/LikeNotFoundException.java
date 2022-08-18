package ru.yandex.practicum.filmorate.exception.like;

public class LikeNotFoundException extends RuntimeException {

    public LikeNotFoundException(String message) {
        super(message);
    }
}
