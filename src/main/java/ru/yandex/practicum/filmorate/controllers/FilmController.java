package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.users.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage storage;
    private final FilmService service;
    private final UserStorage userStorage;

    @Autowired
    public FilmController(FilmStorage storage, FilmService service, UserStorage userStorage) {
        this.storage = storage;
        this.service = service;
        this.userStorage = userStorage;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable int id) {
        return ResponseEntity.ok(storage.getFilm(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilmsList() {
        return ResponseEntity.ok(storage.getFilms());
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(service.getPopularFilms(count));
    }

    @PostMapping
    public ResponseEntity<Film> addNewFilm(@Valid @RequestBody Film film) {
        storage.addFilm(film);
        log.debug("Добавлен новый фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        int id = film.getId();

        storage.updateFilm(film, id);
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
        return userStorage.getUser(userId) == null;
    }
}
