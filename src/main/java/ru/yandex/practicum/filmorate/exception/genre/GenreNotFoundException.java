package ru.yandex.practicum.filmorate.exception.genre;

public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(String message) {
        super(message);
    }
}
