package ru.yandex.practicum.filmorate.service.user.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.feed.FeedDAO;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service("UserDBService")
@Primary
public class UserDBService extends InMemoryUserService {

    public UserDBService(@Qualifier("UserDAO") UserStorage storage,
                         @Qualifier("FeedDAO") FeedDAO feed) {
        super(storage, feed);
    }

    @Override
    public void addFriend(int targetUserId, int friendId) {
        storage.addFriend(targetUserId, friendId);
    }

    @Override
    public void removeFriend(int targetUserId, int friendId) {
        storage.removeFriend(targetUserId, friendId);
    }

    @Override
    public void removeUser(int id) {
        storage.removeUser(id);
    }
}
