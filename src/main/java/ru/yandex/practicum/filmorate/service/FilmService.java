package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.ModelStorage;

import java.util.Collection;

@Service
public class FilmService extends ModelService<Film> {

    private final LikeStorage likeStorage;
    private final ModelStorage<User> userStorage;

    public FilmService(ModelStorage<Film> storage, ModelStorage<User> userStorage) {
        super(storage);
        likeStorage = (LikeStorage)storage;
        this.userStorage = userStorage;
    }

    public int addLike(long id, long userId) {
        final Film film = storage.get(id)
                .orElseThrow(() -> new NotFoundException("Film not found with " + id));
        final User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        likeStorage.addLike(film, user);
        return film.getCountLikes();
    }

    public int deleteLike(long id, long userId) {
        final Film film = storage.get(id)
                .orElseThrow(() -> new NotFoundException("Film not found with " + id));
        final User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        likeStorage.deleteLike(film, user);
        return film.getCountLikes();
    }

    public Collection<Film> getPopular(Integer count) {
        return likeStorage.getPopularFilms(count);
    }
}
