package ru.yandex.practicum.filmorate.exceptions.likes;

public class LikeNotFoundException extends RuntimeException {

    public LikeNotFoundException(String message) {
        super(message);
    }
}
