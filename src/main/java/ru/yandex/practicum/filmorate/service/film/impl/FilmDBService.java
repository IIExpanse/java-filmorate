package ru.yandex.practicum.filmorate.service.film.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.lang.module.FindException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("FilmDBService")
@Primary
public class FilmDBService extends InMemoryFilmService {

    public FilmDBService(@Qualifier("FilmDAO") FilmStorage filmStorage,
                         @Qualifier("UserDAO") UserStorage userStorage) {
        super(filmStorage, userStorage);
    }

    @Override
    public void addLike(int targetFilmId, int userId) {
        filmStorage.addLike(targetFilmId, userId);
    }

    @Override
    public void removeLike(int targetFilmId, int userId) {
        filmStorage.removeLike(targetFilmId, userId);
    }

    @Override
    public Collection<Film> searchFilms(String query, String by) {
        Collection<Film> searchedFilms = List.copyOf(
                filmStorage.searchFilms(query, by).stream()
                        .sorted(Comparator.comparing(Film::getRate).reversed())
                        .collect(Collectors.toList()));
        return searchedFilms;
    }
}