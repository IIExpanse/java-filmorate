package ru.yandex.practicum.filmorate.dao.user;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AllArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "classpath:schematest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:datatest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserDAOTest {

    UserDAO storage;

    @Test
    public void addAndGetUserTest() {
        User user = makeDefaultUser();
        storage.addUser(user);
        user.setId(1);

        assertEquals(user, storage.getUser(1));
    }

    @Test
    public void shouldThrowExceptionForGettingNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> storage.getUser(1));
    }

    @Test
    public void getUsersTest() {
        User user1 = makeDefaultUser();
        storage.addUser(user1);
        user1.setId(1);

        User user2 = makeDefaultUser();
        storage.addUser(user2);
        user2.setId(2);

        assertEquals(List.of(user1, user2), storage.getUsers());
    }

    @Test
    public void addFriendTest() {
        User user1 = makeDefaultUser();
        storage.addUser(user1);

        User user2 = makeDefaultUser();
        storage.addUser(user2);

        storage.addFriend(1, 2);
        assertTrue(storage.getUser(1).getFriendsIds().contains(2));
        assertTrue(storage.getUser(2).getFriendsIds().isEmpty());

        storage.addFriend(2, 1);
        assertTrue(storage.getUser(2).getFriendsIds().contains(1));
    }

    @Test
    public void shouldThrowExceptionsForAddingNonExistentUsersAndFriends() {
        assertThrows(UserNotFoundException.class, () -> storage.addFriend(1, 2));

        User user = makeDefaultUser();
        storage.addUser(user);

        assertThrows(UserNotFoundException.class, () -> storage.addFriend(1, 2));
    }

    @Test
    public void updateUserTest() {
        User user = makeDefaultUser();
        storage.addUser(user);

        user.setName("New name");
        user.setId(1);
        storage.updateUser(user, 1);

        assertEquals(user, storage.getUser(1));
    }

    @Test
    public void shouldThrowExceptionForUpdatingNonExistentUser() {
        User user = makeDefaultUser();
        assertThrows(UserNotFoundException.class, () -> storage.updateUser(user, 1));
    }

    @Test
    public void removeFriendTest() {
        User user1 = makeDefaultUser();
        storage.addUser(user1);

        User user2 = makeDefaultUser();
        storage.addUser(user2);

        storage.addFriend(1, 2);
        assertTrue(storage.getUser(1).getFriendsIds().contains(2));

        storage.removeFriend(1, 2);
        assertTrue(storage.getUser(1).getFriendsIds().isEmpty());
    }

    @Test
    public void shouldThrowExceptionForRemovingNonExistentFriendOrFromAbsentUser() {
        assertThrows(UserNotFoundException.class, () -> storage.removeFriend(1, 2));

        User user = makeDefaultUser();
        storage.addUser(user);

        assertThrows(FriendNotFoundException.class, () -> storage.removeFriend(1, 2));
        assertThrows(UserNotFoundException.class, () -> storage.removeFriend(3, 1));
    }

    private User makeDefaultUser() {
        return new User(
                0,
                "mail@mail.ru",
                "NickName",
                "",
                LocalDate.parse("1946-08-20"));
    }
}
