package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.friend.CantAddSelfException;
import ru.yandex.practicum.filmorate.exception.friend.CantRemoveSelfException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService service;
    private final FilmService filmService;

    public UserController(@Qualifier("UserDBService") UserService service, @Qualifier("FilmDBService") FilmService filmService) {
        this.service = service;
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return ResponseEntity.ok(service.getUser(id));
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsersList() {
        return ResponseEntity.ok(service.getUsers());
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getFriendsList(@PathVariable int id) {
        return ResponseEntity.ok(service.getFriendsList(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return ResponseEntity.ok(service.getCommonFriends(id, otherId));
    }

    @PostMapping
    public ResponseEntity<User> addNewUser(@Valid @RequestBody User user) {
        checkName(user);
        int id = service.addUser(user);
        user.setId(id);
        log.debug("Добавлен новый пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        int id = user.getId();

        checkName(user);
        service.updateUser(user, id);
        log.debug("Обновлена информация о пользователе: {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        if (id == friendId) {
            throw new CantAddSelfException(String.format("Ошибка при добавлении друга для пользователя с id=%d: " +
                    "невозможно добавить в друзья самого себя.", id));
        }
        service.addFriend(id, friendId);
        log.debug("Пользователь с id={} добавлен в список друзей пользователя с id={}.", friendId, id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        if (id == friendId) {
            throw new CantRemoveSelfException(String.format("Ошибка при удалении друга у пользователя с id=%d: " +
                    "невозможно удалить из друзей самого себя.", id));
        }
        service.removeFriend(id, friendId);
        log.debug("Пользователи с id={} и id={} удалены из списков друзей друг друга.", friendId, id);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable int userId) {
        service.removeUser(userId);
        log.debug("Пользователь с id={} удален.", userId);
    }

    @GetMapping("/{id}/recommendation")
    public ResponseEntity<Collection<Film>> getFilmRecommendation(@PathVariable int id) {
        return ResponseEntity.ok(filmService.getFilmRecommendation(id));
    }

    private static void checkName(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("Пустое имя пользователя с id={} изменено на значение логина.", user.getId());
    }
}
