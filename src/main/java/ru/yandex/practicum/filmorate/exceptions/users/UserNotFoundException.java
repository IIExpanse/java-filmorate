package ru.yandex.practicum.filmorate.exceptions.users;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
