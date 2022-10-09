package ru.yandex.practicum.filmorate.service.user.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.feed.FeedDAO;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("InMemoryUserService")
public class InMemoryUserService implements UserService {

    protected final UserStorage storage;
    protected final FeedDAO feed;

    public InMemoryUserService(@Qualifier("InMemoryUserStorage") UserStorage storage,
                               @Qualifier("FeedDAO") FeedDAO feed) {
        this.storage = storage;
        this.feed = feed;
    }

    @Override
    public User getUser(int id) {
        return storage.getUser(id);
    }

    @Override
    public Collection<User> getUsers() {
        return storage.getUsers();
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
    public Collection<Feed> getUserFeed(int userId) {
        return feed.getUserFeed(userId).stream()
                .sorted(Comparator.comparing(Feed::getEventId))
                .collect(Collectors.toList());
    }

    @Override
    public int addUser(User user) {
        return storage.addUser(user);
    }

    @Override
    public void addFriend(int targetUserId, int friendId) {
        User user = storage.getUser(targetUserId);

        user.addFriend(friendId);
    }

    @Override
    public void updateUser(User user, int id) {
        storage.updateUser(user, id);
    }

    @Override
    public void removeFriend(int targetUserId, int friendId) {
        User user = storage.getUser(targetUserId);

        user.removeFriend(friendId);
    }

    @Override
    public void removeUser(int id) {
        storage.removeUser(id);
    }
}
