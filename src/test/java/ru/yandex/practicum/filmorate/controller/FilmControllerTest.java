package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void addNewFilmTest() {
        Film film = makeDefaultFilm();
        ResponseEntity<Film> response = restTemplate.postForEntity(getActualURI(), film, Film.class);
        film.setId(1);

        assertEquals(film, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithInvalidName() {
        Film filmWithWrongName = makeCustomFilm(
                "",
                null,
                null,
                null);
        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(getActualURI(), filmWithWrongName, Film.class).getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithInvalidDescription() {
        String tooLongDescription = "1".repeat(201);
        Film filmWithWrongDescription = makeCustomFilm(
                null,
                tooLongDescription,
                null,
                null);

        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(getActualURI(), filmWithWrongDescription, Film.class).getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithInvalidDate() {
        LocalDate tooLongAgo = LocalDate.of(1453, 9, 29);
        Film filmWithWrongDescription = makeCustomFilm(
                null,
                null,
                tooLongAgo,
                null);

        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(getActualURI(), filmWithWrongDescription, Film.class).getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithInvalidDuration() {
        Film filmWithWrongDescription = makeCustomFilm(
                null,
                null,
                null,
                -1);

        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(getActualURI(), filmWithWrongDescription, Film.class).getStatusCode());
    }

    @Test
    public void updateFilmTest() {
        Film film = addDefaultFilm();
        ResponseEntity<Film> response = restTemplate.exchange(
                getActualURI(),
                HttpMethod.PUT,
                new HttpEntity<>(film),
                Film.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(film, response.getBody());
    }

    @Test
    public void shouldNotUpdateNonexistentFilm() {
        Film film = makeDefaultFilm(1);
        ResponseEntity<Film> response = restTemplate.exchange(
                getActualURI(),
                HttpMethod.PUT,
                new HttpEntity<>(film),
                Film.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getFilmsTest() {
        Film film = addDefaultFilm();
        ResponseEntity<List<Film>> response = restTemplate.exchange(
                getActualURI(),
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(film), response.getBody());
    }

    private Film makeDefaultFilm() {
        return new Film(
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100);
    }

    private Film makeDefaultFilm(int id) {
        Film film = makeDefaultFilm();
        film.setId(id);
        return film;
    }

    private Film makeCustomFilm(String name, String description, LocalDate date, Integer duration) {
        if (name == null) {
            name = "nisi eiusmod";
        }
        if (description == null) {
            description = "adipisicing";
        }
        if (date == null) {
            date = LocalDate.parse("1967-03-25");
        }
        if (duration == null) {
            duration = 100;
        }

        return new Film(
                name,
                description,
                date,
                duration);
    }

    private Film addDefaultFilm() {
        return restTemplate.postForObject(getActualURI(), makeDefaultFilm(), Film.class);
    }

    private String getActualURI() {
        return "http://localhost:" + port + "/films";
    }
}
