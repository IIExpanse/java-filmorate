package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film getFilm(int id);

    Collection<Film> getFilms();

    Genre getGenre(int id);

    List<Genre> getGenres();

    MPA getMPA(int id);

    List<MPA> getMPAs();

    int addFilm(Film film);

    void addLike(int targetFilmId, int userId);

    void updateFilm(Film film, int id);

    void removeLike(int targetFilmId, int userId);

    void removeFilm(int id);
}
