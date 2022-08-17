package ru.yandex.practicum.filmorate.exceptions.friends;

public class FriendAlreadyAddedException extends RuntimeException {

    public FriendAlreadyAddedException(String message) {
        super(message);
    }
}
