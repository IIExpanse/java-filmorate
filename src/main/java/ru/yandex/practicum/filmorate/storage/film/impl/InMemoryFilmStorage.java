package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> filmsMap = new HashMap<>();
    private int idCounter = 1;

    @Override
    public Film getFilm(int id) {
        if (filmsMap.containsKey(id)) {
            return filmsMap.get(id);
        } else throw new FilmNotFoundException(
                String.format("Ошибка получения: фильм с id=%d не найден.", id)
        );
    }

    @Override
    public Collection<Film> getFilms() {
        return List.copyOf(filmsMap.values());
    }

    @Override
    public void addFilm(Film film) {
        int id = generateNewId();

        film.setId(id);
        filmsMap.put(id, film);
    }

    @Override
    public void updateFilm(Film film, int id) {
        if (filmsMap.containsKey(id)) {
            film.setId(id);
            filmsMap.put(id, film);
        } else {
            throw new FilmNotFoundException(
                    String.format("Ошибка обновления: фильм с id=%d не найден.", id)
            );
        }
    }

    private int generateNewId() {
        return idCounter++;
    }
}
