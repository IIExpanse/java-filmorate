package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.SortType;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService service;

    public FilmController(@Qualifier("FilmDBService") FilmService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable int id) {
        return ResponseEntity.ok(service.getFilm(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilmsList() {
        return ResponseEntity.ok(service.getFilms());
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count,
                                                            @RequestParam(defaultValue = "9999") @Positive int genreId,
                                                            @RequestParam(defaultValue = "9999") @Positive @Min(1895) int year) {
        return ResponseEntity.ok(service.getPopularFilms(count, genreId, year));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Film>> searchFilms(@RequestParam String query,
                                                        @RequestParam String by) {
        return ResponseEntity.ok(service.searchFilms(query, by));
    }

    @GetMapping("/director/{id}")
    public ResponseEntity<Collection<Film>> getSortedDirectorFilms(@PathVariable int id,
                                                                   @RequestParam String sortBy) {

        return ResponseEntity.ok(service.getSortedDirectorFilms(id, SortType.valueOf(sortBy.toUpperCase())));
    }

    @GetMapping("/common")
    public ResponseEntity<Collection<Film>> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return ResponseEntity.ok(service.getCommonFilms(userId, friendId));
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable int id) {
        return ResponseEntity.ok(service.getGenre(id));
    }

    @GetMapping("/genres")
    public ResponseEntity<Collection<Genre>> getGenres() {
        return ResponseEntity.ok(service.getGenres());
    }

    @PostMapping
    public ResponseEntity<Film> addNewFilm(@Valid @RequestBody Film film) {
        int id = service.addFilm(film);
        film.setId(id);
        log.debug("Добавлен новый фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        int id = film.getId();

        service.updateFilm(film, id);
        log.debug("Обновлен фильм с id={}", id);
        return ResponseEntity.ok(service.getFilm(film.getId()));
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        service.addLike(id, userId);
        log.debug("Добавлен лайк пользователя с id={} для фильма с id={}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        service.removeLike(id, userId);
        log.debug("Удален лайк пользователя с id={} у фильма с id={}", userId, id);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFilm(@PathVariable int filmId) {
        service.removeFilm(filmId);
        log.debug("Удален фильм с id={}", filmId);
    }
}
