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
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:SchemaTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:DataTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MPAControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final List<MPA> MPAs = List.of(
            new MPA(1, "G"),
            new MPA(2, "PG"),
            new MPA(3, "PG-13"),
            new MPA(4, "R"),
            new MPA(5,"NC-17"));

    @Test
    public void getMPATest() {
        ResponseEntity<MPA> response = restTemplate.getForEntity(getActualURI() + "/1",
                MPA.class);

        assertEquals(MPAs.get(0), response.getBody());
    }

    @Test
    public void getMPAsTest() {
        ResponseEntity<Collection<MPA>> response = restTemplate.exchange(getActualURI(),
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<>() {
                });
        Collection<MPA> list = response.getBody();

        assertEquals(MPAs, list);
    }

    private String getActualURI() {
        return "http://localhost:" + port + "/mpa";
    }

}
