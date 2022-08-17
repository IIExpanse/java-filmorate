package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    void addLike(int targetFilmId, int userId);

    void removeLike(int targetFilmId, int userId);

    Collection<Film> getPopularFilms(int count);
}
