package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface LikeStorage {
    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    Collection<Film> getPopularFilms(int count);

    Collection<Film> getCommonFilmsByUserIdAndFriendId(Long userId, Long friendId);
}
