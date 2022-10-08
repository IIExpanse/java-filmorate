package ru.yandex.practicum.filmorate.storage.film.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmStorageTest {

    InMemoryFilmStorage storage;
    InMemoryUserStorage userStorage;
    Film film;

    @BeforeEach
    public void refreshFilmAndStorage() {
        film = new Film(
                0,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100,
                0,
                new MPA(1, "G"));
        storage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
    }

    @Test
    public void shouldThrowExceptionForNotFoundFilmDuringGetFilm() {
        assertThrows(FilmNotFoundException.class, () -> storage.getFilm(1));
    }

    @Test
    public void addFilmTest() {
        storage.addFilm(film);
        Assertions.assertEquals(film, storage.getFilm(1));
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
                0,
                new MPA(1, "G"));
        storage.updateFilm(film, 1);
        assertEquals("updatedFilm", storage.getFilm(1).getName());
    }

    @Test
    public void shouldThrowExceptionForNotFoundFilmDuringUpdateFIlm() {
        assertThrows(FilmNotFoundException.class, () -> storage.updateFilm(film, 1));
    }

    @Test
    public void getFilmRecommendationTest() {
        Film film1 = makeDefaultFilm();
        film1.setId(1);
        Film film2 = makeDefaultFilm();
        film2.setId(2);
        Film film3 = makeDefaultFilm();
        film3.setId(3);
        User user1 = makeDefaultUser();
        user1.setId(1);
        User user2 = makeDefaultUser();
        user2.setId(2);
        storage.addFilm(film1);
        storage.addFilm(film2);
        storage.addFilm(film3);
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        storage.addLike(1, 1);
        storage.addLike(1, 2);
        storage.addLike(2, 2);
        storage.addLike(3, 2);

        Collection<Film> filmRecommendation = storage.getFilmRecommendation(user1.getId());
        assertNotNull(filmRecommendation);
        assertTrue(filmRecommendation.size() == 2);
    }

    private Film makeDefaultFilm() {
        return new Film(
                0,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100,
                0,
                new MPA(1, "G"));
    }

    private User makeDefaultUser() {
        return new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
    }
}
