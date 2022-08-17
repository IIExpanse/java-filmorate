package ru.yandex.practicum.filmorate.service.film.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.likes.LikeAlreadyAddedException;
import ru.yandex.practicum.filmorate.exceptions.likes.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmServiceTest {

    FilmStorage storage;
    FilmService service;
    Film film;

    @BeforeEach
    public void refreshFields() {
        film = new Film(
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100);
        storage = new InMemoryFilmStorage();
        service = new InMemoryFilmService(storage);
        storage.addFilm(film);
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
                "another film",
                "test film",
                LocalDate.parse("1989-03-25"),
                100);
        storage.addFilm(film1);
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
