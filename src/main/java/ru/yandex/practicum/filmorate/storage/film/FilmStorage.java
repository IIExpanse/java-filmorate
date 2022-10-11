package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.SearchBy;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film getFilm(int id);

    Collection<Film> getFilms();

    Genre getGenre(int id);

    List<Genre> getGenres();

    MPA getMPA(int id);

    List<MPA> getMPAs();

    Director getDirector(int id);

    Collection<Director> getDirectors();

    Collection<Film> getDirectorFilms(int id);

    Collection<Film> getCommonFilms(int userId, int friendId);

    Film addFilm(Film film);

    Director addDirector(Director director);

    void addLike(int targetFilmId, int userId);

    Film updateFilm(Film film, int id);

    Director updateDirector(Director director);

    void removeLike(int targetFilmId, int userId);

    void removeDirector(int id);

    void removeFilm(int id);

    Collection<Film> searchFilms(String query, SearchBy searchBy);

    Collection<Film> getFilmRecommendation(int userId);
}
