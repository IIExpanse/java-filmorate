package ru.yandex.practicum.filmorate.exceptions.friends;

public class CantAddSelfException extends RuntimeException {

    public CantAddSelfException(String message) {
        super(message);
    }
}
