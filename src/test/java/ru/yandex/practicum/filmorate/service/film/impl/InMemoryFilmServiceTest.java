package ru.yandex.practicum.filmorate.service.film.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.like.LikeAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmServiceTest {

    FilmStorage filmStorage;
    UserStorage userStorage;
    FilmService service;
    Film film;

    @BeforeEach
    public void refreshFields() {
        Director director = new Director(1, "Famous Director");

        film = new Film(
                0,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100,
                0,
                new MPA(1, "G"),
                new Director(1, "Famous Director"));
        User user = new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        service = new InMemoryFilmService(filmStorage, userStorage);

        filmStorage.addDirector(director);
        filmStorage.addFilm(film);
        userStorage.addUser(user);
    }

    @Test
    public void addLikeTest() {
        service.addLike(1, 1);
        assertEquals(1, film.getRate());
    }

    @Test
    public void shouldThrowExceptionForAlreadyAddedLike() {
        service.addLike(1, 1);
        assertThrows(LikeAlreadyAddedException.class, () -> service.addLike(1, 1));
    }

    @Test
    public void removeLikeTest() {
        film.addLike(1);
        film.removeLike(1);
        assertEquals(0, film.getRate());
    }

    @Test
    public void shouldThrowExceptionForNotFoundLike() {
        assertThrows(LikeNotFoundException.class, () -> service.removeLike(1, 1));
    }

    @Test
    public void getPopularFilmsTest() {
        Film film1 = new Film(
                0,
                "another film",
                "test film",
                LocalDate.parse("1989-03-25"),
                100,
                0,
                new MPA(1, "G"),
                new Director(1, "Famous Director"));
        filmStorage.addFilm(film1);
        film.addLike(1);
        assertTrue(service.getPopularFilms(10).contains(film));

        film1.addLike(1);
        assertEquals(2, service.getPopularFilms(10).size());
        assertTrue(service.getPopularFilms(10).contains(film1));

        film.addLike(2);
        assertEquals(List.of(film, film1), service.getPopularFilms(10));

        assertEquals(1, service.getPopularFilms(1).size());
    }
}
