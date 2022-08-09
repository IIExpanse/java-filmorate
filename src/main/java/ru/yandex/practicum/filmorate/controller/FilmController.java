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
        int id = idCounter++;
        film.setId(id);
        boolean isPresent = filmsMap.containsKey(id);

        if (!isPresent) {
            filmsMap.put(id, film);
            log.debug("Добавлен новый фильм: {}", film);
            return new ResponseEntity<>(filmsMap.get(id), HttpStatus.CREATED);

        } else {
            String errorMessage = String.format("Добавляемый фильм с id=%d уже существует.", id);
            log.debug("Ошибка при добавлении нового фильма: " + errorMessage);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    errorMessage,
                    new ValidationException(errorMessage));
        }
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        int id = film.getId();

        if (filmsMap.containsKey(id)) {
            filmsMap.put(id, film);
            log.debug("Обновлена информация о фильме: {}", film);
            return new ResponseEntity<>(filmsMap.get(id), HttpStatus.OK);

        } else {
            String errorMessage = String.format("Заменяемый фильм с id=%d не найден.", id);
            log.debug("Ошибка при обновлении информации о фильме: " + errorMessage);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    errorMessage,
                    new ValidationException(errorMessage));
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilmsList() {
        return new ResponseEntity<>(List.copyOf(filmsMap.values()), HttpStatus.OK);
    }
}
