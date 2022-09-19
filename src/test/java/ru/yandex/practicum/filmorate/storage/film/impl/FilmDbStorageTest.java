package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:SchemaTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:DataTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmDbStorageTest {

    FilmDbStorage filmStorage;
    UserDbStorage userStorage;
    private final List<MPA> MPAs = List.of(
            new MPA(1, "G"),
            new MPA(2, "PG"),
            new MPA(3, "PG-13"),
            new MPA(4, "R"),
            new MPA(5,"NC-17"));
    private final List<Genre> genres = List.of(
            new Genre(1, "Комедия"),
            new Genre(2, "Драма"),
            new Genre(3, "Мультфильм"),
            new Genre(4, "Триллер"),
            new Genre(5, "Документальный"),
            new Genre(6, "Боевик")
    );

    @Test
    public void addAndGetFilmTest() {
        Film film = makeDefaultFilm();
        filmStorage.addFilm(film);
        film.setId(1);

        assertEquals(film, filmStorage.getFilm(1));
    }

    @Test
    public void shouldThrowExceptionForGettingNonExistentFilm() {
        assertThrows(FilmNotFoundException.class, () -> filmStorage.getFilm(1));
    }

    @Test
    public void getFilmsTest() {
        Film film1 = makeDefaultFilm();
        film1.setId(1);
        Film film2 = makeDefaultFilm();
        film2.setId(2);
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        assertEquals(List.of(film1, film2), filmStorage.getFilms());
    }

    @Test
    public void getGenreTest() {
        assertEquals(genres.get(0), filmStorage.getGenre(1));
    }

    @Test
    public void getGenresTest() {
        assertEquals(genres, filmStorage.getGenres());
    }

    @Test
    public void getMPATest() {
        assertEquals(MPAs.get(0), filmStorage.getMPA(1));
    }

    @Test
    public void getMPAsTest() {
        assertEquals(MPAs, filmStorage.getMPAs());
    }

    @Test
    public void addLikeTest() {
        Film film = makeDefaultFilm();
        User user = makeDefaultUser();
        filmStorage.addFilm(film);
        userStorage.addUser(user);
        filmStorage.addLike(1, 1);

        assertTrue(filmStorage.getFilm(1).getLikes().contains(1));
    }

    @Test
    public void shouldThrowExceptionForAddingLikeForNonExistentFilm() {
        User user = makeDefaultUser();
        userStorage.addUser(user);

        assertThrows(FilmNotFoundException.class, () -> filmStorage.addLike(1, 1));
    }

    @Test
    public void shouldThrowExceptionForAddingLikeFromNonExistentUser() {
        Film film = makeDefaultFilm();
        filmStorage.addFilm(film);

        assertThrows(UserNotFoundException.class, () -> filmStorage.addLike(1, 2));
    }

    @Test
    public void shouldNotAddFilmWithIncorrectData() {
        Film film1 = new Film(
                0,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100,
                0,
                new MPA(filmStorage.getMPAs().size() + 1, "Abc"));
        assertThrows(DataIntegrityViolationException.class, () -> filmStorage.addFilm(film1));
        Film film2 = makeDefaultFilm();
        film2.setRate(-5);
        filmStorage.addFilm(film2);
        assertThrows(FilmNotFoundException.class, () -> filmStorage.getFilm(1));
    }

    @Test
    public void updateFilmTest() {
        Film film = makeDefaultFilm();
        filmStorage.addFilm(film);
        film.setId(1);
        film.setRate(2);
        filmStorage.updateFilm(film, 1);

        assertEquals(film, filmStorage.getFilm(1));
    }

    @Test
    public void removeLikeTest() {
        Film film = makeDefaultFilm();
        User user = makeDefaultUser();
        filmStorage.addFilm(film);
        userStorage.addUser(user);

        filmStorage.addLike(1, 1);
        assertTrue(filmStorage.getFilm(1).getLikes().contains(1));

        filmStorage.removeLike(1, 1);
        assertTrue(filmStorage.getFilm(1).getLikes().isEmpty());
    }

    @Test
    public void shouldThrowExceptionForRemovingNonExistentLike() {
        assertThrows(FilmNotFoundException.class, () -> filmStorage.removeLike(1, 1));
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
