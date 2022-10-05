package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schematest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:datatest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void addDirector() {
        restTemplate.postForEntity("/directors", new Director(
                1,
                "Famous Director"
        ), Director.class);
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
        film.setId(1);
        assertEquals(List.of(film), response.getBody());
    }

    @Test
    public void getPopularFilmsTest() {
        Film film1 = addDefaultFilm();
        film1.setId(1);
        Film film2 = addDefaultFilm();
        film2.setId(2);
        addDefaultUser();

        addLikeDefault();

        restTemplate.exchange(
                getActualURI() + "/2/like/1",
                HttpMethod.PUT,
                new HttpEntity<>(null),
                Film.class);

        ResponseEntity<Collection<Film>> response = restTemplate.exchange(getActualURI() + "/popular",
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                });
        Collection<Film> list = response.getBody();

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(List.of(film1, film2), list);
    }

    @Test
    public void getSortedDirectorFilms() {
        Film film1 = addDefaultFilm();
        film1.setId(1);

        Film film2 = new Film(
                0,
                "second film",
                "second",
                LocalDate.parse("1944-03-25"),
                100,
                0,
                new MPA(1, "G"));
        film2.addDirector(new Director(1, "Famous Director"));
        restTemplate.postForEntity(getActualURI(), film2, Film.class);
        film2.setId(2);

        Film film3 = new Film(
                0,
                "third film",
                "third",
                LocalDate.parse("1922-03-25"),
                100,
                0,
                new MPA(1, "G"));
        film3.addDirector(new Director(1, "Famous Director"));
        restTemplate.postForEntity(getActualURI(), film3, Film.class);
        film3.setId(3);

        restTemplate.postForEntity("/users", new User(
                0,
                "mail@mail.ru",
                "FirstUser",
                "",
                LocalDate.parse("1966-08-20")), User.class);

        restTemplate.postForEntity("/users", new User(
                0,
                "mail@yandex.ru",
                "SecondUser",
                "",
                LocalDate.parse("1966-08-20")), User.class);

        restTemplate.put(getActualURI() + "/2/like/1", null);
        restTemplate.put(getActualURI() + "/2/like/2", null);
        restTemplate.put(getActualURI() + "/1/like/1", null);

        ResponseEntity<Collection<Film>> sortedByYear = restTemplate.exchange(
                getActualURI() + "/director/1?sortBy=year",
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                }
        );
        ResponseEntity<Collection<Film>> sortedByLikes = restTemplate.exchange(
                getActualURI() + "/director/1?sortBy=likes",
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, sortedByYear.getStatusCode());
        assertEquals(List.of(film3, film2, film1), sortedByYear.getBody());

        assertEquals(HttpStatus.OK, sortedByLikes.getStatusCode());
        assertEquals(List.of(film2, film1, film3), sortedByLikes.getBody());
    }

    @Test
    public void shouldReturnDefaultFilmListIfNothingIsLiked() {
        Film film1 = addDefaultFilm();
        film1.setId(1);
        Film film2 = addDefaultFilm();
        film2.setId(2);
        addDefaultUser();

        ResponseEntity<Collection<Film>> response = restTemplate.exchange(getActualURI() + "/popular",
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                });
        Collection<Film> list = response.getBody();

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(List.of(film1, film2), list);
    }

    @Test
    public void addNewFilmTest() {
        Film film = makeDefaultFilm();
        film.setId(1);
        ResponseEntity<Film> response = restTemplate.postForEntity(getActualURI(), film, Film.class);

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
    public void addLikeTest() {
        addDefaultFilm();
        addDefaultUser();
        HttpStatus responseStatusCode = addLikeDefault().getStatusCode();

        assertEquals(HttpStatus.OK, responseStatusCode);
    }

    @Test
    public void shouldThrowExceptionForAlreadyAddedLike() {
        addDefaultFilm();
        addDefaultUser();
        restTemplate.put(getActualURI() + "/1/like/1", new HttpEntity<>(null));
        HttpStatus statusCode = addLikeDefault().getStatusCode();

        assertEquals(HttpStatus.CONFLICT, statusCode);
    }

    @Test
    public void shouldThrowExceptionForAddingLikeFromNotFoundUser() {
        addDefaultFilm();
        HttpStatus statusCode = addLikeDefault().getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void updateFilmTest() {
        Film film = addDefaultFilm();
        film.setId(1);
        ResponseEntity<Film> response = restTemplate.exchange(
                getActualURI(),
                HttpMethod.PUT,
                new HttpEntity<>(film),
                Film.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(film, response.getBody());
    }

    @Test
    public void shouldThrowExceptionForNotFoundFilmDuringUpdate() {
        Film film = makeDefaultFilm();
        ResponseEntity<Film> response = restTemplate.exchange(
                getActualURI(),
                HttpMethod.PUT,
                new HttpEntity<>(film),
                Film.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void removeLikeTest() {
        addDefaultFilm();
        addDefaultUser();
        restTemplate.put(getActualURI() + "/1/like/1", new HttpEntity<>(null));

        HttpStatus statusCode = restTemplate.exchange(
                        getActualURI() + "/1/like/1",
                        HttpMethod.DELETE,
                        new HttpEntity<>(null),
                        Film.class)
                .getStatusCode();

        assertEquals(HttpStatus.OK, statusCode);
    }

    @Test
    public void shouldThrowExceptionForNotFoundLike() {
        HttpStatus statusCode = restTemplate.exchange(
                        getActualURI() + "/1/like/1",
                        HttpMethod.DELETE,
                        new HttpEntity<>(null),
                        Film.class)
                .getStatusCode();

        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    @Test
    public void removeFilmTest() {
        addDefaultFilm();
        HttpStatus statusCode = restTemplate.exchange(
                        getActualURI() + "/1",
                        HttpMethod.DELETE,
                        new HttpEntity<>(null),
                        Film.class)
                .getStatusCode();

        assertEquals(HttpStatus.OK, statusCode);

        statusCode = restTemplate.getForEntity(getActualURI() + "/1", Film.class).getStatusCode();
        assertEquals(HttpStatus.NOT_FOUND, statusCode);
    }

    private Film makeDefaultFilm() {
        Film film = new Film(
                0,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.parse("1967-03-25"),
                100,
                0,
                new MPA(1, "G"));
        film.addDirector(new Director(1, "Famous Director"));

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
                0,
                name,
                description,
                date,
                duration,
                0,
                new MPA(1, "G"));
    }

    private Film addDefaultFilm() {
        return restTemplate.postForObject(getActualURI(), makeDefaultFilm(), Film.class);
    }

    private String getActualURI() {
        return "http://localhost:" + port + "/films";
    }

    private User makeDefaultUser() {
        return new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20")
        );
    }

    private void addDefaultUser() {
        restTemplate.postForObject("http://localhost:" + port + "/users", makeDefaultUser(), User.class);
    }

    private ResponseEntity<Film> addLikeDefault() {
        return restTemplate.exchange(
                getActualURI() + "/1/like/1",
                HttpMethod.PUT,
                new HttpEntity<>(null),
                Film.class);
    }
}
