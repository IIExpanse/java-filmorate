package ru.yandex.practicum.filmorate.service.user.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InMemoryUserService implements UserService {

    private final UserStorage storage;

    public InMemoryUserService(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public Collection<User> getFriendsList(int targetUserId) {
        User user = storage.getUser(targetUserId);

        return List.copyOf(user.getFriendsIds().stream()
                .map(storage::getUser)
                .collect(Collectors.toList()));
    }

    @Override
    public Collection<User> getCommonFriends(int targetUserId, int otherUserId) {
        User targetUser = storage.getUser(targetUserId);
        User otherUser = storage.getUser(otherUserId);
        Collection<Integer> filterList = otherUser.getFriendsIds();

        return List.copyOf(targetUser.getFriendsIds().stream()
                .filter(filterList::contains)
                .map(storage::getUser)
                .collect(Collectors.toList())
        );
    }

    @Override
    public void addFriend(int targetUserId, int friendId) {
        User user = storage.getUser(targetUserId);
        User friend = storage.getUser(friendId);

        user.addFriend(friendId);
        friend.addFriend(targetUserId);
    }

    @Override
    public void removeFriend(int targetUserId, int friendId) {
        User user = storage.getUser(targetUserId);
        User friend = storage.getUser(friendId);

        user.removeFriend(friendId);
        friend.removeFriend(targetUserId);
    }
}
