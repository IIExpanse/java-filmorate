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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schematest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:datatest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DirectorControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getDirectorTest() {
        Director director = new Director(
                1,
                "Famous Director"
        );

        restTemplate.postForEntity("/directors", director, Integer.class);

        ResponseEntity<Director> response = restTemplate.getForEntity(
                getActualURI() + "/1",
                Director.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(director, response.getBody());
    }

    @Test
    public void getDirectorsTest() {
        Director director1 = new Director(
                1,
                "Famous Director"
        );
        restTemplate.postForEntity("/directors", director1, Integer.class);

        Director director2 = new Director(
                2,
                "Second Director"
        );
        restTemplate.postForEntity("/directors", director2, Integer.class);

        ResponseEntity<Collection<Director>> response = restTemplate.exchange(
                getActualURI(),
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(director1, director2), response.getBody());
    }

    @Test
    public void addDirectorTest() {
        Director director = new Director(1, "Famous Director");

        ResponseEntity<Director> response = restTemplate.postForEntity("/directors", director, Director.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(director, response.getBody());
    }

    @Test
    public void updateDirectorTest() {
        restTemplate.postForEntity("/directors", new Director(
                1,
                "Famous Director"
        ), Integer.class);

        Director director = new Director(1, "Second Director");
        ResponseEntity<Director> response = restTemplate.exchange(
                getActualURI(),
                HttpMethod.PUT,
                new HttpEntity<>(director),
                Director.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(director, response.getBody());
    }

    private String getActualURI() {
        return "http://localhost:" + port + "/directors";
    }
}
