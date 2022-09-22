package ru.yandex.practicum.filmorate.dao.mpa;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.film.FilmDAO;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schematest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:datatest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MpaDAOTest {

    FilmDAO filmStorage;

    private final List<MPA> MPAs = List.of(
            new MPA(1, "G"),
            new MPA(2, "PG"),
            new MPA(3, "PG-13"),
            new MPA(4, "R"),
            new MPA(5,"NC-17"));

    @Test
    public void getMPATest() {
        assertEquals(MPAs.get(0), filmStorage.getMPA(1));
    }

    @Test
    public void getMPAsTest() {
        assertEquals(MPAs, filmStorage.getMPAs());
    }
}
