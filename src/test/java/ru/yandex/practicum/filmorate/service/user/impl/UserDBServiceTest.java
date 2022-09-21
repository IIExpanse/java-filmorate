package ru.yandex.practicum.filmorate.service.user.impl;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.friend.FriendAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.user.UserDAO;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Sql(scripts = "classpath:SchemaTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:DataTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserDBServiceTest {

    UserDAO storage;
    UserDBService service;

    @Test
    public void addFriendTest() {
        User user1 = makeAndAddUser();
        user1.setId(1);
        User user2 = makeAndAddUser();
        user2.setId(2);

        service.addFriend(user1.getId(), user2.getId());
        assertTrue(service.getUser(user1.getId()).getFriendsIds().contains(user2.getId()));
    }

    @Test
    public void shouldThrowExceptionForAlreadyAddedFriend() {
        User user1 = makeAndAddUser();
        user1.setId(1);
        User user2 = makeAndAddUser();
        user2.setId(2);

        service.addFriend(user1.getId(), user2.getId());
        assertThrows(FriendAlreadyAddedException.class, () -> service.addFriend(user1.getId(), user2.getId()));
    }

    @Test
    public void removeFriendTest() {
        User user1 = makeAndAddUser();
        user1.setId(1);
        User user2 = makeAndAddUser();
        user2.setId(2);

        int id1 = user1.getId();
        int id2 = user2.getId();

        service.addFriend(id1, id2);
        service.removeFriend(id1, id2);
        assertTrue(user1.getFriendsIds().isEmpty());
        assertTrue(user2.getFriendsIds().isEmpty());
    }

    @Test
    public void shouldThrowExceptionForNotFoundFriend() {
        User user1 = makeAndAddUser();
        user1.setId(1);
        User user2 = makeAndAddUser();
        user2.setId(2);

        assertThrows(FriendNotFoundException.class, () -> service.removeFriend(user1.getId(), user2.getId()));
    }

    @Test
    public void getFriendsListTest() {
        User user1 = makeAndAddUser();
        user1.setId(1);
        User user2 = makeAndAddUser();
        user2.setId(2);

        service.addFriend(user1.getId(), user2.getId());
        assertTrue(service.getFriendsList(user1.getId()).contains(user2));
    }

    @Test
    public void getCommonFriendsTest() {
        User user1 = makeAndAddUser();
        user1.setId(1);
        User user2 = makeAndAddUser();
        user2.setId(2);
        User user3 = new User(
                0,
                "mail@somemail.ru",
                "Jackdog",
                "",
                LocalDate.parse("1933-08-20"));
        user3.setId(3);
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

    private User makeAndAddUser() {
        User user = new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
        storage.addUser(user);
        return user;
    }
}
