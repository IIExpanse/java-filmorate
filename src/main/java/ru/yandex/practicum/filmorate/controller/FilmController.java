package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> filmsMap = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public ResponseEntity<Film> addNewFilm(@Valid @RequestBody Film film) throws ValidationException {
        int id = generateNewId();

        film.setId(id);
        filmsMap.put(id, film);
        log.debug("Добавлен новый фильм: {}", film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);

    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film, @RequestParam int id) throws ValidationException {

        if (filmsMap.containsKey(id)) {
            filmsMap.put(id, film);
            log.debug("Обновлена информация о фильме: {}", film);
            return new ResponseEntity<>(filmsMap.get(id), HttpStatus.OK);

        } else {
            String errorMessage = String.format("Заменяемый фильм с id=%d не найден.", id);
            log.debug("Ошибка при обновлении информации о фильме с id={}", id);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    errorMessage,
                    new ValidationException(errorMessage));
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilmsList() {
        return ResponseEntity.ok(List.copyOf(filmsMap.values()));
    }

    private int generateNewId() {
        return idCounter++;
    }
}
