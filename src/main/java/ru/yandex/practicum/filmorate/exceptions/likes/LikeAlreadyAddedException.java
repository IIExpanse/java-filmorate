package ru.yandex.practicum.filmorate.exceptions.likes;

public class LikeAlreadyAddedException extends RuntimeException {
    public LikeAlreadyAddedException(String message) {
        super(message);
    }
}
