package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    void addFriend(int targetUserId, int friendId);

    void removeFriend(int targetUserId, int friendId);

    Collection<User> getFriendsList(int targetUserId);

    Collection<User> getCommonFriends(int targetUserId, int otherUserId);
}
