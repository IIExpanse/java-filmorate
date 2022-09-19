package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.friend.FriendAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    public void refreshUser() {
        user = new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
    }

    @Test
    public void addFriendTest() {
        user.addFriend(1);
        assertTrue(user.getFriendsIds().contains(1));
    }

    @Test
    public void shouldThrowExceptionForAddingSameUserTwice() {
        user.addFriend(1);
        assertThrows(FriendAlreadyAddedException.class, () -> user.addFriend(1));
    }

    @Test
    public void removeFriendTest() {
        user.addFriend(1);
        user.removeFriend(1);
        assertFalse(user.getFriendsIds().contains(1));
    }

    @Test
    public void shouldThrowExceptionForRemovingNotFoundFriend() {
        assertThrows(FriendNotFoundException.class, () -> user.removeFriend(1));
    }
}
