package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film getFilm(int id);

    Collection<Film> getFilms();

    Collection<Film> getPopularFilms(int count, int genreId, int year);

    Genre getGenre(int id);

    List<Genre> getGenres();

    MPA getMpa(int id);

    List<MPA> getMPAs();

    Director getDirector(int id);

    Collection<Director> getDirectors();

    Collection<Film> getSortedDirectorFilms(int id, SortType sortType);

    Collection<Film> getCommonFilms(int firstUserId, int secondUserId);

    Film addFilm(Film film);

    void addLike(int targetFilmId, int userId);

    Director addDirector(Director director);

    Film updateFilm(Film film, int id);

    Director updateDirector(Director director);

    void removeLike(int targetFilmId, int userId);

    void removeDirector(int id);

    void removeFilm(int id);

    Collection<Film> searchFilms(String query, SearchBy searchBy);

    Collection<Film> getFilmRecommendation(int userId);
}
