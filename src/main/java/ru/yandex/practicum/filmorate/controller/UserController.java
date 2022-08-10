package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> usersMap = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public ResponseEntity<User> addNewUser(@Valid @RequestBody User user) throws ValidationException {
        checkName(user);
        int id = generateNewId();

        user.setId(id);
        usersMap.put(id, user);
        log.debug("Добавлен новый пользователь: {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user, @RequestParam int id) throws ValidationException {
        checkName(user);
        boolean isPresent = usersMap.containsKey(id);

        if (isPresent) {
            user.setId(id);
            usersMap.put(id, user);
            log.debug("Обновлена информация о пользователе: {}", user);
            return new ResponseEntity<>(usersMap.get(id), HttpStatus.OK);

        } else {
            String errorMessage = String.format("Заменяемый пользователь с id=%d не найден.", id);
            log.debug("Ошибка при обновлении информации о пользователе с id={}", id);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    errorMessage,
                    new ValidationException(errorMessage));
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsersList() {
        return ResponseEntity.ok(List.copyOf(usersMap.values()));
    }

    private static void checkName(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("Пустое имя пользователя с id={} изменено на значение логина.", user.getId());
    }

    private int generateNewId() {
        return idCounter++;
    }
}
