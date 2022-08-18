package ru.yandex.practicum.filmorate.service.user.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.friend.CantAddSelfException;
import ru.yandex.practicum.filmorate.exception.friend.FriendAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserServiceTest {

    UserStorage storage;
    UserService service;
    User user1;
    User user2;

    @BeforeEach
    public void refreshFields() {
        user1 = new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
        user2 = new User(
                0,
                "mail@yandex.ru",
                "Tomcat",
                "",
                LocalDate.parse("1978-08-20"));
        storage = new InMemoryUserStorage();
        service = new InMemoryUserService(storage);
        storage.addUser(user1);
        storage.addUser(user2);
    }

    @Test
    public void addFriendTest() {
        service.addFriend(user1.getId(), user2.getId());
        assertTrue(user1.getFriendsIds().contains(user2.getId()));
        assertTrue(user2.getFriendsIds().contains(user1.getId()));
    }

    @Test
    public void shouldThrowExceptionForAlreadyAddedFriend() {
        service.addFriend(user1.getId(), user2.getId());
        assertThrows(FriendAlreadyAddedException.class, () -> service.addFriend(user1.getId(), user2.getId()));
    }

    @Test
    public void shouldThrowExceptionForAddingSelfToFriends() {
        int id = user1.getId();
        assertThrows(CantAddSelfException.class, () -> service.addFriend(id, id));
    }

    @Test
    public void removeFriendTest() {
        int id1 = user1.getId();
        int id2 = user2.getId();

        service.addFriend(id1, id2);
        service.removeFriend(id1, id2);
        assertTrue(user1.getFriendsIds().isEmpty());
        assertTrue(user2.getFriendsIds().isEmpty());
    }

    @Test
    public void shouldThrowExceptionForNotFoundFriend() {
        assertThrows(FriendNotFoundException.class, () -> service.removeFriend(user1.getId(), user2.getId()));
    }

    @Test
    public void getFriendsListTest() {
        service.addFriend(user1.getId(), user2.getId());
        assertTrue(service.getFriendsList(user1.getId()).contains(user2));
    }

    @Test
    public void getCommonFriendsTest() {
        User user3 = new User(
                0,
                "mail@somemail.ru",
                "Jackdog",
                "",
                LocalDate.parse("1933-08-20"));
        storage.addUser(user3);

        int id1 = user1.getId();
        int id2 = user2.getId();
        int id3 = user3.getId();

        service.addFriend(id1, id3);
        service.addFriend(id2, id3);

        Collection<User> commonFriendsList = service.getCommonFriends(id1, id2);
        assertEquals(1, commonFriendsList.size());
        assertTrue(commonFriendsList.contains(user3));
    }
}
