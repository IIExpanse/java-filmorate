package ru.yandex.practicum.filmorate.dao.film;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.user.UserDAO;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schematest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:datatest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmDAOTest {

    FilmDAO filmStorage;
    UserDAO userStorage;

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
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(3, 2);

        Collection<Film> filmRecommendation = filmStorage.getFilmRecommendation(user1.getId());
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
