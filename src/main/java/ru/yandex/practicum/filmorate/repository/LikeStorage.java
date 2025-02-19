package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface LikeStorage {
    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    Collection<Film> getCommonFilmsByUserIdAndFriendId(Long userId, Long friendId);

    Collection<Film> getPopularByGenreIdAndYear(int count, Long genreId, Integer year);

    Set<Film> getLikedFilmsByUser(long userId);
}
