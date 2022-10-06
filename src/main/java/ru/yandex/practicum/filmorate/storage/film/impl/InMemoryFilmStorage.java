package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Repository(value = "InMemoryFilmStorage")
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
    public Genre getGenre(int id) {
        return getGenres().get(id - 1);
    }

    @Override
    public List<Genre> getGenres() {
        return List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик")
        );
    }

    @Override
    public MPA getMPA(int id) {
        return getMPAs().get(id - 1);
    }

    @Override
    public List<MPA> getMPAs() {
        return List.of(
                new MPA(1, "G"),
                new MPA(2, "PG"),
                new MPA(3, "PG-13"),
                new MPA(4, "R"),
                new MPA(5,"NC-17")
        );
    }

    @Override
    public int addFilm(Film film) {
        int id = generateNewId();

        film.setId(id);
        filmsMap.put(id, film);
        return id;
    }

    @Override
    public void addLike(int targetFilmId, int userId) {
        filmsMap.get(targetFilmId).addLike(userId);
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

    @Override
    public void removeLike(int targetFilmId, int userId) {
        filmsMap.get(targetFilmId).removeLike(userId);
    }

    @Override
    public Collection<Film> getFilmRecommendation(int userId) {
        Collection<Film> recommendedFilms = new ArrayList<>();
        Set<Integer> userIdWithCommonFilmLikes = new HashSet<>();
        Collection<Film> allFilms = getFilms();
        for (Film film : allFilms) {
            Set<Integer> likes = film.getLikes();
            if (likes == null) {
                return allFilms;
            }
            for (Integer user : likes) {
                if (user == userId) {
                    for (int i : likes) {
                        if (i != userId) {
                            userIdWithCommonFilmLikes.add(i);
                        }
                    }

                }
            }
        }
        for (Film film : allFilms) {
            Set<Integer> likes = film.getLikes();
            for (Integer user : likes) {
                for (Integer userWithCommonInterest : userIdWithCommonFilmLikes) {
                    if (user == userWithCommonInterest) {
                        for (Integer i : likes) {
                            if (i == userId) {
                                break;
                            } else if (i != userId) {
                                recommendedFilms.add(film);;
                            }
                        }

                    }
                }

            }
        }
        return recommendedFilms;
    }

    private int generateNewId() {
        return idCounter++;
    }
}
