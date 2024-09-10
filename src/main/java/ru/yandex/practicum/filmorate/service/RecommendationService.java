package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.ModelRepository;
import java.util.*;

@Service
@Slf4j
public class RecommendationService {

    private final FilmService filmService;
    private final ModelRepository<User> userRepository;


    public RecommendationService(FilmService filmService, ModelRepository<User> userRepository) {
        this.filmService = filmService;
        this.userRepository = userRepository;
    }

    public Collection<Film> getRecommendations(long userId) {
        log.info("Получение рекомендаций для пользователя с id {}", userId);
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + userId));
        log.info("Пользователь найден: {}", user);
        Collection<User> allUsers = userRepository.getAll();
        log.info("Получены все пользователи: {}", allUsers);
        User similarUser = findMostSimilarUser(user, allUsers);
        if (similarUser == null) {
            log.info("Нет похожего пользователя для рекомендаций");
            return Collections.emptyList();
        }
        log.info("Найден похожий пользователь: {}", similarUser);
        return findFilmsToRecommend(user, similarUser);
    }


    private User findMostSimilarUser(User currentUser, Collection<User> allUsers) {
        log.info("Поиск наиболее похожего пользователя для пользователя с id {}", currentUser.getId());
        User mostSimilarUser = null;
        int maxCommonLikes = 0;
        for (User otherUser : allUsers) {
            if (currentUser.getId() != otherUser.getId()) {
                int commonLikes = countCommonLikes(currentUser, otherUser);
                log.info("Количество общих лайков между пользователями {} и {}: {}", currentUser.getId(), otherUser.getId(), commonLikes);
                if (commonLikes > maxCommonLikes) {
                    maxCommonLikes = commonLikes;
                    mostSimilarUser = otherUser;
                }
            }
        }
        log.info("Найден наиболее похожий пользователь: {}", mostSimilarUser);
        return mostSimilarUser;
    }

    private int countCommonLikes(User user1, User user2) {
        Set<Film> user1Likes = filmService.getLikedFilms(user1.getId());
        Set<Film> user2Likes = filmService.getLikedFilms(user2.getId());
        Set<Film> commonLikes = new HashSet<>(user1Likes);
        commonLikes.retainAll(user2Likes);
        return commonLikes.size();
    }

    private Collection<Film> findFilmsToRecommend(User currentUser, User similarUser) {
        log.info("Поиск фильмов для рекомендации пользователю с id {}", currentUser.getId());
        Set<Film> currentUserLikes = filmService.getLikedFilms(currentUser.getId());
        log.info("Фильмы, которые лайкнул пользователь с id {}: {}", currentUser.getId(), currentUserLikes);
        Set<Film> similarUserLikes = filmService.getLikedFilms(similarUser.getId());
        log.info("Фильмы, которые лайкнул пользователь с id {}: {}", similarUser.getId(), similarUserLikes);
        Set<Film> recommendedFilms = new HashSet<>(similarUserLikes);
        recommendedFilms.removeAll(currentUserLikes);
        log.info("Рекомендуемые фильмы для пользователя с id {}: {}", currentUser.getId(), recommendedFilms);
        return recommendedFilms;
    }
}
