package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

public interface UserRepository extends ModelRepository<User>, FriendStorage, DeleteStorage {
}
