package ru.yandex.practicum.filmorate.service;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DeleteStorage;
import ru.yandex.practicum.filmorate.repository.FriendStorage;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

import java.util.Collection;

@Service
public class UserService extends ModelService<User> {
    private final FriendStorage friendStorage;
    private final DeleteStorage deleteStorage;

    public UserService(ModelRepository<User> storage) {
        super(storage);
       friendStorage = (FriendStorage)storage;
       deleteStorage = (DeleteStorage)storage;
    }

    public void addFriend(long userId, long friendId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        final User friend = repository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with " + friendId));
        friendStorage.addFriend(user, friend);
    }

    public void deleteFriend(long userId, long friendId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        final User friend = repository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with " + friendId));
       friendStorage.deleteFriend(user, friend);
    }

    public Collection<User> getFriends(long userId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        return friendStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        return friendStorage.getCommonFriends(userId, otherUserId);
    }

    public void delete(long id) {
        deleteStorage.delete(id);
    }
}
