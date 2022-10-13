package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    User getUser(int id);

    Collection<User> getUsers();

    Collection<User> getFriendsList(int targetUserId);

    Collection<User> getCommonFriends(int targetUserId, int otherUserId);

    Collection<Feed> getUserFeed(int userId);

    User addUser(User user);

    void addFriend(int targetUserId, int friendId);

    User updateUser(User user, int id);

    void removeFriend(int targetUserId, int friendId);

    void removeUser(int id);
}
