package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:SchemaTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:DataTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class GenreControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final List<Genre> genres = List.of(
            new Genre(1, "Комедия"),
            new Genre(2, "Драма"),
            new Genre(3, "Мультфильм"),
            new Genre(4, "Триллер"),
            new Genre(5, "Документальный"),
            new Genre(6, "Боевик")
    );

    @Test
    public void getGenreTest() {
        ResponseEntity<Genre> response = restTemplate.getForEntity(getActualURI() + "/1",
                Genre.class);

        assertEquals(genres.get(0), response.getBody());
    }

    @Test
    public void getGenresTest() {
        ResponseEntity<Collection<Genre>> response = restTemplate.exchange(getActualURI(),
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                });
        Collection<Genre> list = response.getBody();

        assertEquals(genres, list);
    }

    private String getActualURI() {
        return "http://localhost:" + port + "/genres";
    }

}
