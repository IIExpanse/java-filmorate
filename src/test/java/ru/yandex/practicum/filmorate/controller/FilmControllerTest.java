package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {

    private static final String context = "/films";
    private static final String testURI = FilmorateApplicationTests.BASE_URL + context;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void addNewFilmTest() {
        Film film = makeDefaultFilm();
        Film filmWithId = makeDefaultFilm(1);
        ResponseEntity<Film> response = restTemplate.postForEntity(testURI, film, Film.class);

        assertEquals(filmWithId, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithInvalidName() {
        Film filmWithWrongName = makeCustomFilm(
                null,
                "",
                null,
                null,
                null);
        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(testURI, filmWithWrongName, Film.class).getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithInvalidDescription() {
        String tooLongDescription = "1".repeat(201);
        Film filmWithWrongDescription = makeCustomFilm(
                null,
                null,
                tooLongDescription,
                null,
                null);

        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(testURI, filmWithWrongDescription, Film.class).getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithInvalidDate() {
        LocalDate tooLongAgo = LocalDate.of(1453, 9, 29);
        Film filmWithWrongDescription = makeCustomFilm(
                null,
                null,
                null,
                tooLongAgo,
                null);

        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(testURI, filmWithWrongDescription, Film.class).getStatusCode());
    }

    @Test
    public void shouldNotAddFilmWithInvalidDuration() {
        Film filmWithWrongDescription = makeCustomFilm(
                null,
                null,
                null,
                null,
                -1);

        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(testURI, filmWithWrongDescription, Film.class).getStatusCode());
    }

    @Test
//    @Disabled
    public void updateFilmTest() {
        Film film = addDefaultFilm();
        ResponseEntity<Film> response = restTemplate.exchange(
                testURI,
                HttpMethod.PUT,
                new HttpEntity<>(film),
                Film.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(film, response.getBody());
    }

    /*      TODO: 09.08.2022 Тест вызывает ошибку SocketException: Unexpected end of file from server
             при запуске с другими тестами, использующими exchange.
             TODO: Изменить ответ от сервера и посмотреть разницу. */
    @Test
    public void shouldNotUpdateNonexistentFilm() {
        Film film = makeDefaultFilm(1);
        ResponseEntity<Film> response = restTemplate.exchange(
                testURI,
                HttpMethod.PUT,
                new HttpEntity<>(film),
                Film.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
//    @Disabled
    public void getFilmsTest() {
        Film film = addDefaultFilm();
        ResponseEntity<List<Film>> response = restTemplate.exchange(
                testURI,
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

    private Film makeCustomFilm(Integer id, String name, String description, LocalDate date, Integer duration) {
        if (id == null) {
            id = 0;
        }
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
                id,
                name,
                description,
                date,
                duration);
    }

    private Film addDefaultFilm() {
        return restTemplate.postForObject(testURI, makeDefaultFilm(), Film.class);
    }
}
