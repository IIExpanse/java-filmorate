package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> usersMap = new HashMap<>();
    private int idCounter = 1;

    @Override
    public User getUser(int id) {
        if (usersMap.containsKey(id)) {
            return usersMap.get(id);
        } else throw new UserNotFoundException(
                String.format("Ошибка получения: пользователь с id=%d не найден.", id)
        );
    }

    @Override
    public Collection<User> getUsers() {
        return List.copyOf(usersMap.values());
    }

    @Override
    public User addUser(User user) {
        int id = generateNewId();

        user.setId(id);
        usersMap.put(id, user);
        return user;
    }

    @Override
    public void addFriend(int targetUserId, int friendId) {
        User user = getUser(targetUserId);
        user.addFriend(friendId);
    }

    @Override
    public User updateUser(User user, int id) {
        if (usersMap.containsKey(id)) {
            user.setId(id);
            usersMap.put(id, user);

        } else throw new UserNotFoundException(
                    String.format("Ошибка обновления: пользователь с id=%d не найден.", id));
        return user;
    }

    @Override
    public void removeFriend(int targetUserId, int friendId) {
        User user = getUser(targetUserId);
        user.removeFriend(friendId);
    }

    @Override
    public void removeUser(int id) {
        usersMap.remove(id);
    }

    private int generateNewId() {
        return idCounter++;
    }
}
