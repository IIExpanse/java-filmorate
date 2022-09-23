package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User getUser(int id);

    Collection<User> getUsers();

    int addUser(User user);

    void addFriend(int targetUserId, int friendId);

    void updateUser(User user, int id);

    void removeFriend(int targetUserId, int friendId);
}
