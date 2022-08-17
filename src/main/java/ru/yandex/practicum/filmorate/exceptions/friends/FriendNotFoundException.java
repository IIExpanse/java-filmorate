package ru.yandex.practicum.filmorate.exceptions.friends;

public class FriendNotFoundException extends RuntimeException {

    public FriendNotFoundException(String message) {
        super(message);
    }
}
