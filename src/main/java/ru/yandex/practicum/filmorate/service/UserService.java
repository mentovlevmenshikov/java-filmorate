package ru.yandex.practicum.filmorate.service;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.ModelStorage;

import java.util.Collection;

@Service
public class UserService extends ModelService<User> {
    private final FriendStorage friendStorage;

    public UserService(ModelStorage<User> storage) {
        super(storage);
        friendStorage = (FriendStorage)storage;
    }

    public void addFriend(long userId, long friendId) {
        final User user = storage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        final User friend = storage.get(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with " + friendId));
        friendStorage.addFriend(user, friend);
    }

    public void deleteFriend(long userId, long friendId) {
        final User user = storage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        final User friend = storage.get(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with " + friendId));
        friendStorage.deleteFriend(user, friend);
    }

    public Collection<User> getFriends(long userId) {
        final User user = storage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        return friendStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        return friendStorage.getCommonFriends(userId, otherUserId);
    }
}
