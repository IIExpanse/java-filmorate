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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    private static final String context = "/users";
    private static final String testURI = FilmorateApplicationTests.BASE_URL + context;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void addNewUserTest() {
        User user = makeDefaultUser();
        User userWithId = makeDefaultUser(1);
        ResponseEntity<User> response = restTemplate.postForEntity(testURI, user, User.class);

        assertEquals(userWithId, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void shouldNotAddUserWithInvalidEmail() {
        User userWithWrongEmail = makeCustomUser(
                null,
                "mail",
                null,
                null,
                null);
        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(testURI, userWithWrongEmail, User.class).getStatusCode());
    }

    @Test
    public void shouldNotAddUserWithInvalidWhitespaceLogin() {
        User userWithWrongEmail = makeCustomUser(
                null,
                null,
                "lo gin",
                null,
                null);
        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(testURI, userWithWrongEmail, User.class).getStatusCode());
    }

    @Test
    public void shouldNotAddUserWithInvalidBlankLogin() {
        User userWithWrongEmail = makeCustomUser(
                null,
                null,
                "",
                null,
                null);
        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(testURI, userWithWrongEmail, User.class).getStatusCode());
    }

    @Test
    public void shouldReplaceBlankNameWithLogin() {
        User userWithWrongEmail = makeCustomUser(
                null,
                null,
                "login",
                "",
                null);
        assertEquals("login",
                restTemplate.postForEntity(testURI, userWithWrongEmail, User.class).getBody().getName());
    }

    @Test
    public void shouldNotAddUserWithInvalidBirthday() {
        User userWithWrongEmail = makeCustomUser(
                null,
                null,
                null,
                null,
                LocalDate.ofYearDay(40000, 20));
        assertEquals(HttpStatus.BAD_REQUEST,
                restTemplate.postForEntity(testURI, userWithWrongEmail, User.class).getStatusCode());
    }

    @Test
    public void updateUserTest() {
        User user = addDefaultUser();
        ResponseEntity<User> response = restTemplate.exchange(
                testURI,
                HttpMethod.PUT,
                new HttpEntity<>(user),
                User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void shouldNotUpdateNonexistentUser() {
        User user = makeDefaultUser(1);
        ResponseEntity<User> response = restTemplate.exchange(
                testURI,
                HttpMethod.PUT,
                new HttpEntity<>(user),
                User.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getUsersTest() {
        User user = addDefaultUser();
        ResponseEntity<List<User>> response = restTemplate.exchange(
                testURI,
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(user), response.getBody());
    }

    private User makeDefaultUser() {
        return new User(
                "mail@mail.ru",
                "NickName",
                "dolore",
                LocalDate.parse("1946-08-20")
        );
    }

    private User makeDefaultUser(int id) {
        User user = makeDefaultUser();
        user.setId(id);
        return user;
    }

    private User makeCustomUser(Integer id, String email, String login, String name, LocalDate birthday) {
        if (id == null) {
            id = 0;
        }
        if (email == null) {
            email = "mail@mail.ru";
        }
        if (login == null) {
            login = "Nick Name";
        }
        if (name == null) {
            name = "dolore";
        }
        if (birthday == null) {
            birthday = LocalDate.parse("1946-08-20");
        }

        return new User(
                id,
                email,
                login,
                name,
                birthday);
    }

    private User addDefaultUser() {
        return restTemplate.postForObject(testURI, makeDefaultUser(), User.class);
    }
}
