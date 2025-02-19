package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Service
public class UserService extends ModelService<User> {
    private final UserRepository userRepository;
    private final EventFeedService eventFeedService;

    public UserService(UserRepository repository, EventFeedService eventFeedService) {
        super(repository);
       userRepository = repository;
       this.eventFeedService = eventFeedService;
    }

    public void addFriend(long userId, long friendId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        final User friend = repository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with " + friendId));
        userRepository.addFriend(user, friend);
        eventFeedService.addEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        final User friend = repository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Friend not found with " + friendId));
       userRepository.deleteFriend(user, friend);
       eventFeedService.addEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public Collection<User> getFriends(long userId) {
        final User user = repository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        return userRepository.getFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        return userRepository.getCommonFriends(userId, otherUserId);
    }

    public void delete(long id) {
        userRepository.delete(id);
    }
}
