package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendStorage {
    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long userId, long otherUserId);
}
