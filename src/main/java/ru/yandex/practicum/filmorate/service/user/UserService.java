package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    User getUser(int id);

    Collection<User> getUsers();

    Collection<User> getFriendsList(int targetUserId);

    Collection<User> getCommonFriends(int targetUserId, int otherUserId);

    int addUser(User user);

    void addFriend(int targetUserId, int friendId);

    void updateUser(User user, int id);

    void removeFriend(int targetUserId, int friendId);
}
