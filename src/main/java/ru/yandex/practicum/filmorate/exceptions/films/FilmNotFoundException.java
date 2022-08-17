package ru.yandex.practicum.filmorate.exceptions.films;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(String message) {
        super(message);
    }
}
