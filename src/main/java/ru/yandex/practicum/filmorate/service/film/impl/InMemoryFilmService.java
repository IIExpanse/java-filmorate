package ru.yandex.practicum.filmorate.service.film.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


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
                        .dropWhile(film -> film.getRate() == 0)
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
    public void addLike(int targetFilmId, int userId) {
        if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException(
                    String.format("Ошибка при добавлении лайка к фильму с id=%d от пользователя с id=%d: " +
                            "пользователь не найден.", targetFilmId, userId)
            );
        }
        Film film = filmStorage.getFilm(targetFilmId);
        film.addLike(userId);
    }

    @Override
    public void addFilm(Film film) {
        filmStorage.addFilm(film);
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
        Film film = filmStorage.getFilm(targetFilmId);
        film.removeLike(userId);
    }
}
