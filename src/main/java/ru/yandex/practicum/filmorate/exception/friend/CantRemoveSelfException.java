package ru.yandex.practicum.filmorate.exception.friend;

public class CantRemoveSelfException extends RuntimeException {
    public CantRemoveSelfException(String message) {
        super(message);
    }
}
