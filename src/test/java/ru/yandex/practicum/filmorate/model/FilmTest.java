package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.likes.LikeAlreadyAddedException;
import ru.yandex.practicum.filmorate.exceptions.likes.LikeNotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {

    private Film film;

    @BeforeEach
    public void refreshFilm() {
        film = new Film(
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100);
    }

    @Test
    public void addLikeTest() {
        film.addLike(1);

        assertTrue(film.getLikes().contains(1));
    }

    @Test
    public void shouldThrowExceptionForAlreadyAddedLike() {
        film.addLike(1);

        assertThrows(LikeAlreadyAddedException.class, () -> film.addLike(1));
    }

    @Test
    public void removeLikeTest() {
        film.addLike(1);
        film.removeLike(1);
        assertFalse(film.getLikes().contains(1));
    }

    @Test
    public void shouldThrowExceptionForNotFoundLike() {
        assertThrows(LikeNotFoundException.class, () -> film.removeLike(1));
    }
}
