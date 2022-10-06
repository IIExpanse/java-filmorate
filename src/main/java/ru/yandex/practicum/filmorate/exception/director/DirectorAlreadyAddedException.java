package ru.yandex.practicum.filmorate.exception.director;

public class DirectorAlreadyAddedException extends RuntimeException {

    public DirectorAlreadyAddedException(String message) {
        super(message);
    }
}
