package ru.yandex.practicum.filmorate.service.film.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("InMemoryFilmService")
public class InMemoryFilmService implements FilmService {

    protected final FilmStorage filmStorage;
    protected final UserStorage userStorage;

    public InMemoryFilmService(@Qualifier("InMemoryFilmStorage") FilmStorage filmStorage,
                               @Qualifier("InMemoryUserStorage") UserStorage userStorage) {
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
    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> popularFilmsList = List.copyOf(
                filmStorage.getFilms().stream()
                        .sorted(Comparator.comparing(Film::getRate).reversed())
                        .limit(count)
                        .collect(Collectors.toList()));

        if (popularFilmsList.isEmpty()) {
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
    public void addLike(int targetFilmId, int userId) {
        if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException(
                    String.format("Ошибка при добавлении лайка к фильму с id=%d от пользователя с id=%d: " +
                            "пользователь не найден.", targetFilmId, userId)
            );
        }
        filmStorage.addLike(targetFilmId, userId);
    }

    @Override
    public int addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public void updateFilm(Film film, int id) {
        filmStorage.updateFilm(film, id);
    }

    @Override
    public void removeLike(int targetFilmId, int userId) {
        if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException(
                    String.format("Ошибка при удалении лайка у фильма с id=%d от пользователя с id=%d: " +
                            "пользователь не найден.", targetFilmId, userId)
            );
        }
        filmStorage.removeLike(targetFilmId, userId);
    }


    public Collection<Film> getFilmRecommendation(int userId) {
        return filmStorage.getFilmRecommendation(userId);
    }
}
