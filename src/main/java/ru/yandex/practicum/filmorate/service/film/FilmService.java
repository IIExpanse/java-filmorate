package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    Film getFilm(int id);

    Collection<Film> getFilms();

    Collection<Film> getPopularFilms(int count);

    void addFilm(Film film);

    void addLike(int targetFilmId, int userId);

    void updateFilm(Film film, int id);

    void removeLike(int targetFilmId, int userId);
}
