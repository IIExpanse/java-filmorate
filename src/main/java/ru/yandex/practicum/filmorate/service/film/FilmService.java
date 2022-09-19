package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film getFilm(int id);

    Collection<Film> getFilms();

    Collection<Film> getPopularFilms(int count);

    Genre getGenre(int id);

    List<Genre> getGenres();

    MPA getMpa(int id);

    List<MPA> getMPAs();

    int addFilm(Film film);

    void addLike(int targetFilmId, int userId);

    void updateFilm(Film film, int id);

    void removeLike(int targetFilmId, int userId);
}
