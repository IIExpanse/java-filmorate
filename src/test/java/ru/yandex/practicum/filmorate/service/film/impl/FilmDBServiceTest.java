package ru.yandex.practicum.filmorate.service.film.impl;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.like.LikeAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.user.UserDAO;
import ru.yandex.practicum.filmorate.service.film.SortType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Sql(scripts = "classpath:schematest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:datatest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class FilmDBServiceTest {

    UserDAO userStorage;
    FilmDBService service;

    @BeforeEach
    public void addDirector() {
        Director director = new Director(1, "Famous Director");
        service.addDirector(director);
    }

    @Test
    public void addLikeTest() {
        addUserAndFilm();

        service.addLike(1, 1);
        assertEquals(1, service.getFilm(1).getRate());
    }

    @Test
    public void shouldThrowExceptionForAlreadyAddedLike() {
        addUserAndFilm();
        service.addLike(1, 1);
        assertThrows(LikeAlreadyAddedException.class, () -> service.addLike(1, 1));
    }

    @Test
    public void removeLikeTest() {
        addUserAndFilm();
        service.addLike(1, 1);
        service.removeLike(1, 1);
        assertEquals(0, service.getFilm(1).getRate());
    }

    @Test
    public void shouldThrowExceptionForNotFoundLike() {
        addUserAndFilm();
        assertThrows(LikeNotFoundException.class, () -> service.removeLike(1, 1));
    }

    @Test
    public void getPopularFilmsTest() {
        Film film = addUserAndFilm();
        film.setId(1);

        service.addLike(1, 1);
        assertEquals(1, service.getPopularFilms(1).size());
        assertTrue(service.getPopularFilms(1).contains(service.getFilm(1)));
    }

    @Test
    public void getSortedDirectorFilmsTest() {
        Film film1 = addUserAndFilm();
        Film film2 = new Film(
                0,
                "second film",
                "second",
                LocalDate.parse("1944-03-25"),
                100,
                0,
                new MPA(1, "G"),
                new Director(1, "Famous Director"));
        service.addFilm(film2);
        film2.setId(2);

        Film film3 = new Film(
                0,
                "third film",
                "third",
                LocalDate.parse("1922-03-25"),
                100,
                0,
                new MPA(1, "G"),
                new Director(1, "Famous Director"));
        service.addFilm(film3);
        film3.setId(3);

        User user = new User(
                0,
                "mail@yandex.ru",
                "SecondUser",
                "",
                LocalDate.parse("1966-08-20"));
        userStorage.addUser(user);

        service.addLike(2, 1);
        film2.addLike(1);
        service.addLike(2, 2);
        film2.addLike(2);
        service.addLike(1, 1);
        film1.addLike(1);

        assertEquals(List.of(film2, film1, film3), service.getSortedDirectorFilms(1, SortType.LIKES));
        assertEquals(List.of(film1, film2, film3), service.getSortedDirectorFilms(1, SortType.YEAR));
    }

    private Film addUserAndFilm() {
        Film film = new Film(
                0,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100,
                0,
                new MPA(1, "G"),
                new Director(1, "Famous Director"));
        film.setId(1);
        User user = new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
        service.addFilm(film);
        userStorage.addUser(user);
        return film;
    }
}
