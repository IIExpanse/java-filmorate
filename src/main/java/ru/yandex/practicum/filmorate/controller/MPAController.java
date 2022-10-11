package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MPAController {

    private final FilmService service;

    @GetMapping("/{id}")
    public ResponseEntity<MPA> getMPA(@PathVariable int id) {
        return ResponseEntity.ok(service.getMpa(id));
    }

    @GetMapping
    public ResponseEntity<Collection<MPA>> getMPAs() {
        return ResponseEntity.ok(service.getMPAs());
    }
}
