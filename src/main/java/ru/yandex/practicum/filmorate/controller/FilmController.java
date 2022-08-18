package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
@AllArgsConstructor
public class FilmController {

    private final FilmService service;
    private ServletWebServerApplicationContext context;

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable int id) {
        return ResponseEntity.ok(service.getFilm(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilmsList() {
        return ResponseEntity.ok(service.getFilms());
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(service.getPopularFilms(count));
    }

    @PostMapping
    public ResponseEntity<Film> addNewFilm(@Valid @RequestBody Film film) {
        service.addFilm(film);
        log.debug("Добавлен новый фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        int id = film.getId();

        service.updateFilm(film, id);
        log.debug("Обновлен фильм с id={}", id);
        return ResponseEntity.ok(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        if (isUserNotFound(userId)) {
            throw new UserNotFoundException(
                    String.format("Ошибка при добавлении лайка к фильму с id=%d от пользователя с id=%d: " +
                            "пользователь не найден.", id, userId)
            );
        }
        service.addLike(id, userId);
        log.debug("Добавлен лайк пользователя с id={} для фильма с id={}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        if (isUserNotFound(userId)) {
            throw new UserNotFoundException(
                    String.format("Ошибка при удалении лайка у фильма с id=%d от пользователя с id=%d: " +
                            "пользователь не найден.", id, userId)
            );
        }
        service.removeLike(id, userId);
        log.debug("Удален лайк пользователя с id={} у фильма с id={}", userId, id);
    }

    private boolean isUserNotFound(int userId) {
        RestTemplate template = new RestTemplate();
        int port = context.getWebServer().getPort();
        try {
            template.getForObject("http://localhost:" + port + "/users/" + userId, User.class);

        } catch (HttpClientErrorException e) {
            return true;
        }

        return false;
    }
}
