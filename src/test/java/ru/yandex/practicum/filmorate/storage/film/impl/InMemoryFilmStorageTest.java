package ru.yandex.practicum.filmorate.storage.film.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmStorageTest {

    FilmStorage storage;
    Film film;

    @BeforeEach
    public void refreshFilmAndStorage() {
        film = new Film(
                0,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100,
                0);
        storage = new InMemoryFilmStorage();
    }

    @Test
    public void shouldThrowExceptionForNotFoundFilmDuringGetFilm() {
        assertThrows(FilmNotFoundException.class, () -> storage.getFilm(1));
    }

    @Test
    public void addFilmTest() {
        storage.addFilm(film);
        assertEquals(film, storage.getFilm(1));
    }

    @Test
    public void getFilmsTest() {
        storage.addFilm(film);
        assertTrue(storage.getFilms().contains(film));
    }

    @Test
    public void updateFilmTest() {
        storage.addFilm(film);
        film = new Film(
                0,
                "updatedFilm",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100,
                0);
        storage.updateFilm(film, 1);
        assertEquals("updatedFilm", storage.getFilm(1).getName());
    }

    @Test
    public void shouldThrowExceptionForNotFoundFilmDuringUpdateFIlm() {
        assertThrows(FilmNotFoundException.class, () -> storage.updateFilm(film, 1));
    }
}
