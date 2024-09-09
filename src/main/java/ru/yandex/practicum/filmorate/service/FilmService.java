package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService extends ModelService<Film> {

    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final ModelRepository<Director> directorRepository;
    private final List<String> sortFeatures = List.of("year", "likes");
    private final EventFeedService eventFeedService;

    public FilmService(FilmRepository filmRepository, UserRepository userRepository,
                       DirectorRepository directorRepository, EventFeedService eventFeedService) {
        super(filmRepository);
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.directorRepository = directorRepository;
        this.eventFeedService = eventFeedService;
    }

    public int addLike(long id, long userId) {
        final Film film = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with " + id));
        final User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        filmRepository.addLike(film, user);
        eventFeedService.addEvent(userId, EventType.LIKE, EventOperation.ADD, id);
        return film.getCountLikes();
    }

    public int deleteLike(long id, long userId) {
        final Film film = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with " + id));
        final User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        filmRepository.deleteLike(film, user);
        eventFeedService.addEvent(userId, EventType.LIKE, EventOperation.REMOVE, id);
        return film.getCountLikes();
    }

    public Collection<Film> getPopular(Integer count) {
        return filmRepository.getPopularFilms(count);
    }

    public void delete(long id) {
        filmRepository.delete(id);
    }

    public Collection<Film> getByDirector(long directorId, String sortBy) {
        final Director director = directorRepository.getById(directorId)
                .orElseThrow(() -> new NotFoundException("Director not found with " + directorId));

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = sortFeatures.getFirst();
        }
        if (!sortFeatures.contains(sortBy)) {
            throw new ValidationException("Sorting by " + sortBy + " not allowed");
        }
        return filmRepository.getFilmsByDirector(directorId, sortBy);
    }

    public Collection<Film> getByQuery(FilmSearchBy searchBy, String query) {
        return
                switch (searchBy) {
                    case DIRECTOR -> filmRepository.searchByDirector(query);
                    case TITLE -> filmRepository.searchByTitle(query);
                    case DIRECTOR_TITLE_BOTH -> filmRepository.searchByDirectorTitleBoth(query);
        };
    }
}
