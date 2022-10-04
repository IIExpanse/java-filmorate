package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {

    private final FilmService service;

    public DirectorController(@Qualifier("FilmDBService") FilmService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirector(@PathVariable int id) {
        return ResponseEntity.ok(service.getDirector(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Director>> getDirectors() {
        return ResponseEntity.ok(service.getDirectors());
    }

    @PostMapping
    public ResponseEntity<Integer> addDirector(@RequestBody Director director) {
        int id = service.addDirector(director);
        log.debug("Добавлен новый режиссер: {}", new Director(id, director.getName()));

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Director> updateDirector(@RequestBody Director director) {
        service.updateDirector(director);
        log.debug("Обновлены данные о директоре с id={}", director.getId());

        return ResponseEntity.ok(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDirector(@PathVariable int id) {
        service.removeDirector(id);
        log.debug("Удалены данные о режиссере с id={}", id);
    }
}
