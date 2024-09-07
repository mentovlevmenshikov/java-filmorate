package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.validator.Update;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor // 2
public class ReviewController { // extends Controller<Review> { //2
    private final ReviewService reviewService;

    /*public ReviewController(ValidationService<Review> validationService, ReviewService reviewService) {
        super(validationService, reviewService);
        this.reviewService = reviewService;
    }*/
  //  @Override
  /*  @GetMapping
    public Collection<Review> getAll() {
        log.info("Запрос всех отзывов");
        Collection<Review> allFilms = modelService.getAll();
        log.info("Возврат отзывов в кол-ве: {}", allFilms.size());
        return allFilms;
    }*/
    @GetMapping()
    public Collection<Review> getReviewsCount(@RequestParam(required = false) Integer filmId,@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Получение отзывов по фильму {} в кол-ве: {}",filmId, count);
        Collection<Review>  review = reviewService.getReviews(filmId,count);
        log.info("Выбрано отзывов по фильмову {}", filmId);
        return reviewService.getReviews(filmId, count);
    }

  //  @Override
    @GetMapping("/{id}")
    public Review get(@PathVariable long id) {

        log.info("Запрос отзыва по id: {}", id);
        Review review = reviewService.get(id);
        log.info("Возврат отзыва: {}", review);
        return review;
    }

   // @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@RequestBody @Valid Review review) {
        Review reviewCreated = null;
        log.info("Создание отзыва: {}", review);
      //  validationService.validate4Create(review);
        if (reviewService.checkDataReview(review)) {
            reviewCreated = reviewService.create(review);
            log.info("Создан отзыв: {}", reviewCreated);
        }
        return reviewCreated;
    }
    @PutMapping
    public Review update(@RequestBody @Validated(Update.class) Review review) {
        log.info("Обновление отзыва: {}", review);
       // validationService.validate4Update(review);
        Review updated = reviewService.update(review);
        log.info("Обновленный фильм: {}", updated);
        return updated;
    }

   /* @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Удаление отзыва с id {}", id);
        modelService
    }*/
   @PutMapping("/{id}/like/{userId}")
   public void addLike(@PathVariable long id, @PathVariable long userId) {
       log.info("Добавление лайка отзыву с id {} пользователем {}", id, userId);
       int countLikes = reviewService.addLike(id, userId);
       log.info("Кол-во лайков у отзыва: {}", countLikes);
   }
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка у отзыва с id {} пользователем {}", id, userId);
        int countLikes = reviewService.deleteLike(id, userId);
        log.info("Кол-во лайков у отзыва: {}", countLikes);
    }
}
