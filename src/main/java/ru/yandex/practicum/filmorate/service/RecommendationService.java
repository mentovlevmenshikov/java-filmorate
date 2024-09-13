package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecommendationService {

    private final FilmService filmService;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public Collection<Film> getRecommendations(long userId) {
        log.info("Получение рекомендаций для пользователя с id {}", userId);
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id " + userId));
        log.info("Пользователь найден: {}", user);
        User similarUser = findMostSimilarUser(user);
        if (similarUser == null) {
            log.info("Нет похожего пользователя для рекомендаций");
            return Collections.emptyList();
        }
        log.info("Найден похожий пользователь: {}", similarUser);
        return findFilmsToRecommend(user, similarUser);
    }

    private User findMostSimilarUser(User currentUser) {
        log.info("Поиск наиболее похожего пользователя для пользователя с id {}", currentUser.getId());
        User mostSimilarUser = userRepository.getMostSimilarUserByLikeFilm(currentUser);
        log.info("Найден наиболее похожий пользователь с id: {}", mostSimilarUser);
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
