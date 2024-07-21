package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage extends InMemoryModelStorage<User> implements  FriendStorage {

    protected final Map<Long, Set<Long>> userFriends = new HashMap<>();

    @Override
    public void addFriend(User user, User friend) {
        Set<Long> userFriendIds = userFriends.computeIfAbsent(user.getId(), id -> new HashSet<>());
        userFriendIds.add(friend.getId());

        Set<Long> friendFriendIds = userFriends.computeIfAbsent(friend.getId(), id -> new HashSet<>());
        friendFriendIds.add(user.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        Set<Long> userFriendIds = userFriends.computeIfAbsent(user.getId(), id -> new HashSet<>());
        userFriendIds.remove(friend.getId());

        Set<Long> friendFriendIds = userFriends.computeIfAbsent(friend.getId(), id -> new HashSet<>());
        friendFriendIds.add(friend.getId());
    }

    @Override
    public Collection<User> getFriends(long userId) {
        Collection<User> friends = new HashSet<>();
        Set<Long> friendIds = userFriends.get(userId);
        if (friendIds != null) {
            for (long id : friendIds) {
                friends.add(models.get(id));
            }
        }
        return friends;
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        Collection<User> commonFriends = new HashSet<>();
        Set<Long> userFriendIds = userFriends.get(userId);
        Set<Long> otherUserFriendIds = userFriends.get(otherUserId);
        if (userFriendIds != null && otherUserFriendIds != null) {
            for (long id : userFriendIds) {
                if (otherUserFriendIds.contains(id)) {
                    commonFriends.add(models.get(id));
                }
            }
        }
        return commonFriends;
    }
}
