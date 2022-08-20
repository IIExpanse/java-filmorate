package ru.yandex.practicum.filmorate.storage.user.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserStorageTest {

    UserStorage storage;
    User user;

    @BeforeEach
    public void refreshUser() {
        user = new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
        storage = new InMemoryUserStorage();
    }

    @Test
    public void shouldThrowExceptionForNotFoundUserDuringGetUser() {
        assertThrows(UserNotFoundException.class, () -> storage.getUser(1));
    }

    @Test
    public void addUserTest() {
        storage.addUser(user);
        assertEquals(user, storage.getUser(1));
    }

    @Test
    public void getUsersTest() {
        storage.addUser(user);
        assertTrue(storage.getUsers().contains(user));
    }

    @Test
    public void updateUserTest() {
        storage.addUser(user);
        user = new User(
                0,
                "yandex@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
        storage.updateUser(user, 1);
        assertEquals("yandex@mail.ru", storage.getUser(1).getEmail());
    }

    @Test
    public void shouldThrowExceptionForNotFoundUserDuringUpdateUser() {
        assertThrows(UserNotFoundException.class, () -> storage.updateUser(user, 1));
    }
}
