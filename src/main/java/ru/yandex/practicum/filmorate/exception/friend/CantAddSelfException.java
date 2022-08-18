package ru.yandex.practicum.filmorate.exception.friend;

public class CantAddSelfException extends RuntimeException {

    public CantAddSelfException(String message) {
        super(message);
    }
}
