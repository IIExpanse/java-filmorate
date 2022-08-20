package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User getUser(int id);

    Collection<User> getUsers();

    void addUser(User user);

    void updateUser(User user, int id);
}
