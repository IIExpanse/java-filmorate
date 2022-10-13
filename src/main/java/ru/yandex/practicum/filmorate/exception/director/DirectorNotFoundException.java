package ru.yandex.practicum.filmorate.exception.director;

public class DirectorNotFoundException extends RuntimeException {

    public DirectorNotFoundException(String message) {
        super(message);
    }
}
