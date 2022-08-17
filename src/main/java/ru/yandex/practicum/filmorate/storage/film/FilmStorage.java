package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film getFilm(int id);

    Collection<Film> getFilms();

    void addFilm(Film film);

    void updateFilm(Film film, int id);
}
