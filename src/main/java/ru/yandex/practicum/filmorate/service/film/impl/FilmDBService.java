package ru.yandex.practicum.filmorate.service.film.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.SortType;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("FilmDBService")
public class FilmDBService implements FilmService {

    protected final FilmStorage filmStorage;
    protected final UserStorage userStorage;

    public FilmDBService(@Qualifier("FilmDAO") FilmStorage filmStorage,
                         @Qualifier("UserDAO") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public Collection<Film> getPopularFilms(int count, int genreId, int year) {
        Collection<Film> popularFilmsList = filmStorage.getFilms();
        if (genreId != 9999) {
            popularFilmsList = popularFilmsList.stream()
                    .filter(f -> f.getGenres().contains(getGenre(genreId)))
                    .collect(Collectors.toList());
        }
        if (year != 9999) {
            popularFilmsList = popularFilmsList.stream()
                    .filter(f -> f.getReleaseDate().getYear() == year)
                    .collect(Collectors.toList());
        }
        popularFilmsList = popularFilmsList.stream()
                .sorted(Comparator.comparing(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());

        if (popularFilmsList.isEmpty() && (genreId == 9999 && year == 9999)) {
            popularFilmsList = filmStorage.getFilms().stream()
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return popularFilmsList;
    }

    @Override
    public Genre getGenre(int id) {
        return filmStorage.getGenre(id);
    }

    @Override
    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    @Override
    public MPA getMpa(int id) {
        return filmStorage.getMPA(id);
    }

    @Override
    public List<MPA> getMPAs() {
        return filmStorage.getMPAs();
    }

    @Override
    public Director getDirector(int id) {
        return filmStorage.getDirector(id);
    }

    @Override
    public Collection<Director> getDirectors() {
        return filmStorage.getDirectors();
    }

    @Override
    public Collection<Film> getSortedDirectorFilms(int id, SortType sortType) {
        Collection<Film> list = filmStorage.getDirectorFilms(id);

        if (sortType == SortType.YEAR) {
            return list.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .collect(Collectors.toList());
        } else return list.stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getCommonFilms(int firstUserId, int secondUserId) {
        return filmStorage.getCommonFilms(firstUserId, secondUserId);
    }

    @Override
    public void addLike(int targetFilmId, int userId) {
        filmStorage.addLike(targetFilmId, userId);
    }

    @Override
    public int addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Director addDirector(Director director) {
        return filmStorage.addDirector(director);
    }

    @Override
    public void updateFilm(Film film, int id) {
        filmStorage.updateFilm(film, id);
    }

    @Override
    public void updateDirector(Director director) {
        filmStorage.updateDirector(director);
    }

    @Override
    public void removeLike(int targetFilmId, int userId) {
        filmStorage.removeLike(targetFilmId, userId);
    }

    @Override
    public void removeDirector(int id) {
        filmStorage.removeDirector(id);
    }

    @Override
    public void removeFilm(int id) {
        filmStorage.removeFilm(id);
    }

    @Override
    public Collection<Film> searchFilms(String query, String by) {
        Collection<Film> searchedFilms;
        if (by.contains("director") && by.contains("title")) {
            searchedFilms = filmStorage.searchFilms(query, SortType.DIRECTOR_AND_TITLE);
        } else if (SortType.valueOf(by.toUpperCase()) == SortType.TITLE) {
            searchedFilms = filmStorage.searchFilms(query, SortType.TITLE);
        } else {
            searchedFilms = filmStorage.searchFilms(query, SortType.DIRECTOR);
        }
        return searchedFilms.stream()
                .sorted(Comparator.comparing(Film::getRate).reversed())
                .collect(Collectors.toList());
    }

    public Collection<Film> getFilmRecommendation(int userId) {
        return filmStorage.getFilmRecommendation(userId);
    }
}
