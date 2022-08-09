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
        int id = idCounter++;
        user.setId(id);
        checkName(user);

        if (!usersMap.containsKey(id)) {
            usersMap.put(id, user);
            log.debug("Добавлен новый пользователь: {}", user);
            return new ResponseEntity<>(usersMap.get(id), HttpStatus.CREATED);

        } else {
            String errorMessage = String.format("Добавляемый пользователь с id=%d уже существует", id);
            log.debug("Ошибка при добавлении нового пользователя" + errorMessage);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    errorMessage,
                    new ValidationException(errorMessage));
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) throws ValidationException {
        checkName(user);
        int id = user.getId();
        boolean isPresent = usersMap.containsKey(id);

        if (isPresent) {
            usersMap.put(id, user);
            log.debug("Обновлена информация о пользователе: {}", user);
            return new ResponseEntity<>(usersMap.get(id), HttpStatus.OK);

        } else {
            String errorMessage = String.format("Заменяемый пользователь с id=%d не найден.", id);
            log.debug("Ошибка при обновлении информации о пользователе: " + errorMessage);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    errorMessage,
                    new ValidationException(errorMessage));
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsersList() {
        return new ResponseEntity<>(List.copyOf(usersMap.values()), HttpStatus.OK);
    }

    private static void checkName(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("Пустое имя пользователя с id={} изменено на значение логина.", user.getId());
    }
}
