package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.SearchBy;
import ru.yandex.practicum.filmorate.service.film.SortType;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
@AllArgsConstructor
public class FilmController {

    private final FilmService service;

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
        SearchBy searchBy;
        String director = SearchBy.DIRECTOR.toString();
        String title = SearchBy.TITLE.toString();
        by = by.toUpperCase();

        if (by.contains(director) && by.contains(title)) {
            searchBy = SearchBy.DIRECTOR_AND_TITLE;

        } else if (by.contains(director)) {
            searchBy = SearchBy.DIRECTOR;

        } else if (by.contains(title)) {
            searchBy = SearchBy.TITLE;

        } else throw new IllegalArgumentException("Ошибка: указан некорректный тип поиска.");

        return ResponseEntity.ok(service.searchFilms(query, searchBy));
    }

    @GetMapping("/director/{id}")
    public ResponseEntity<Collection<Film>> getSortedDirectorFilms(@PathVariable int id,
                                                                   @RequestParam String sortBy) {
        SortType sortType;
        try {
            sortType = SortType.valueOf(sortBy.toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка: указан некорректный тип сортировки.");
        }

        return ResponseEntity.ok(service.getSortedDirectorFilms(id, sortType));
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
        film = service.addFilm(film);

        log.debug("Добавлен новый фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        film = service.updateFilm(film, film.getId());

        log.debug("Обновлен фильм с id={}", film.getId());
        return ResponseEntity.ok(film);
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
