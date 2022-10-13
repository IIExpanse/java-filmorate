package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User getUser(int id);

    Collection<User> getUsers();

    User addUser(User user);

    void addFriend(int targetUserId, int friendId);

    User updateUser(User user, int id);

    void removeFriend(int targetUserId, int friendId);

    void removeUser(int id);
}
