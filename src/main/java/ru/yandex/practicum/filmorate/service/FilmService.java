package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.LikeStorage;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

import java.util.Collection;

@Service
public class FilmService extends ModelService<Film> {

    private final LikeStorage likeStorage;
    private final ModelRepository<User> userModelRepository;

    public FilmService(ModelRepository<Film> filmModelRepository, ModelRepository<User> userModelRepository) {
        super(filmModelRepository);
        likeStorage = (LikeStorage)filmModelRepository;
        this.userModelRepository = userModelRepository;
    }

    public int addLike(long id, long userId) {
        final Film film = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with " + id));
        final User user = userModelRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        likeStorage.addLike(film, user);
        return film.getCountLikes();
    }

    public int deleteLike(long id, long userId) {
        final Film film = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with " + id));
        final User user = userModelRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        likeStorage.deleteLike(film, user);
        return film.getCountLikes();
    }

    public Collection<Film> getPopular(Integer count) {
        return likeStorage.getPopularFilms(count);
    }
}
