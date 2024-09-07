package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.DeleteStorage;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.LikeStorage;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService extends ModelService<Film> {

    private final LikeStorage likeStorage;
    private final ModelRepository<User> userModelRepository;
    private final FilmStorage filmStorage;
    private final ModelRepository<Director> directorModelRepository;
    private final List<String> sortFeatures = List.of("year", "likes");
    private final DeleteStorage deleteStorage;
    private final EventFeedService eventFeedService;

    public FilmService(ModelRepository<Film> filmModelRepository, ModelRepository<User> userModelRepository,
                       ModelRepository<Director> directorModelRepository, EventFeedService eventFeedService) {
        super(filmModelRepository);
        likeStorage = (LikeStorage) filmModelRepository;
        filmStorage = (FilmStorage) filmModelRepository;
        this.userModelRepository = userModelRepository;
        this.directorModelRepository = directorModelRepository;
        deleteStorage = (DeleteStorage)filmModelRepository;
        this.eventFeedService = eventFeedService;
    }

    public int addLike(long id, long userId) {
        final Film film = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with " + id));
        final User user = userModelRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        likeStorage.addLike(film, user);
        eventFeedService.addEvent(userId, EventType.LIKE, EventOperation.ADD, id);
        return film.getCountLikes();
    }

    public int deleteLike(long id, long userId) {
        final Film film = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with " + id));
        final User user = userModelRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        likeStorage.deleteLike(film, user);
        eventFeedService.addEvent(userId, EventType.LIKE, EventOperation.REMOVE, id);
        return film.getCountLikes();
    }

    public Collection<Film> getPopular(Integer count) {
        return likeStorage.getPopularFilms(count);
    }

    public void delete(long id) {
        deleteStorage.delete(id);
    }

    public Collection<Film> getByDirector(long directorId, String sortBy) {
        final Director director = directorModelRepository.getById(directorId)
                .orElseThrow(() -> new NotFoundException("Director not found with " + directorId));

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = sortFeatures.getFirst();
        }
        if (!sortFeatures.contains(sortBy)) {
            throw new ValidationException("Sorting by " + sortBy + " not allowed");
        }
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }
}
