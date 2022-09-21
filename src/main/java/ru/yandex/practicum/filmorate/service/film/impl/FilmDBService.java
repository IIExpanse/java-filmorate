package ru.yandex.practicum.filmorate.service.film.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
}