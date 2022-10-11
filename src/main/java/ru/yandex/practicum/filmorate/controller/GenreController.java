package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenreController {

    private final FilmService service;

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable int id) {
        return ResponseEntity.ok(service.getGenre(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Genre>> getGenres() {
        return ResponseEntity.ok(service.getGenres());
    }
}
