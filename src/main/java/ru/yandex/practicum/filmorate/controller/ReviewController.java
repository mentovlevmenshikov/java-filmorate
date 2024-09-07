package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.validator.Update;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping()
    public Collection<Review> getReviewsCount(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Получение отзывов по фильму {} в кол-ве: {}", filmId, count);
        Collection<Review> review = reviewService.getReviews(filmId, count);
        log.info("Выбрано отзывов по фильмову {}", filmId);
        return reviewService.getReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable long id) {

        log.info("Запрос отзыва по id: {}", id);
        Review review = reviewService.get(id);
        log.info("Возврат отзыва: {}", review);
        return review;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@RequestBody @Valid Review review) {
        Review reviewCreated = null;
        log.info("Создание отзыва: {}", review);
        if (reviewService.checkDataReview(review)) {
            reviewCreated = reviewService.create(review);
            log.info("Создан отзыв: {}", reviewCreated);
        }
        return reviewCreated;
    }

    @PutMapping
    public Review update(@RequestBody @Validated(Update.class) Review review) {
        log.info("Обновление отзыва: {}", review);
        Review updated = reviewService.update(review);
        log.info("Обновленный отзыв: {}", updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Удаление отзыва с id {}", id);
        reviewService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Добавление лайка отзыву с id {} пользователем {}", id, userId);
        int countLikes = reviewService.addLike(id, userId);
        log.info("Кол-во лайков у отзыва: {}", countLikes);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDisLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Добавление лайка отзыву с id {} пользователем {}", id, userId);
        int countLikes = reviewService.addDisLike(id, userId);
        log.info("Кол-во лайков у отзыва: {}", countLikes);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка у отзыва с id {} пользователем {}", id, userId);
        int countLikes = reviewService.deleteLike(id, userId);
        log.info("Кол-во лайков у отзыва: {}", countLikes);
    }
}

