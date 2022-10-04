package ru.yandex.practicum.filmorate.dao.director;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.film.FilmDAO;
import ru.yandex.practicum.filmorate.exception.director.DirectorAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schematest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:datatest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DirectorDAOTest {

    FilmDAO filmDAO;

    @Test
    public void shouldThrowExceptionForNotFoundDirector() {
        assertThrows(DirectorNotFoundException.class, () -> filmDAO.getDirector(1));
    }

    @Test
    public void addDirectorTest() {
        Director director = makeDefaultDirector();

        filmDAO.addDirector(director);
        assertEquals(director, filmDAO.getDirector(1));
    }

    @Test
    public void shouldThrowExceptionForAlreadyAddedDirector() {
        Director director = makeDefaultDirector();

        filmDAO.addDirector(director);
        assertThrows(DirectorAlreadyAddedException.class, () -> filmDAO.addDirector(director));
    }

    @Test
    public void addDirectorsTest() {
        Director director1 = makeDefaultDirector();
        Director director2 = new Director(2, "yuktr");

        filmDAO.addDirector(director1);
        filmDAO.addDirector(director2);

        assertEquals(List.of(director1, director2), filmDAO.getDirectors());
    }

    @Test
    public void updateDirectorTest() {
        Director director = makeDefaultDirector();
        Director director2 = new Director(1, "yuktr");
        filmDAO.addDirector(director);
        filmDAO.updateDirector(director2);

        assertEquals(director2, filmDAO.getDirector(1));
    }

    @Test
    public void removeDirectorTest() {
        Director director = makeDefaultDirector();
        filmDAO.addDirector(director);
        filmDAO.removeDirector(director.getId());

        assertThrows(DirectorNotFoundException.class, () -> filmDAO.getDirector(1));
    }

    @Test
    public void shouldThrowExceptionForRemovingNonExistentDirector() {
        assertThrows(DirectorNotFoundException.class, () -> filmDAO.removeDirector(1));
    }

    private Director makeDefaultDirector() {
        return new Director(1, "rthr");
    }
}
