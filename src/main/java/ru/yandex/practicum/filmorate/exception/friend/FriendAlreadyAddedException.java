package ru.yandex.practicum.filmorate.exception.friend;

public class FriendAlreadyAddedException extends RuntimeException {

    public FriendAlreadyAddedException(String message) {
        super(message);
    }
}
