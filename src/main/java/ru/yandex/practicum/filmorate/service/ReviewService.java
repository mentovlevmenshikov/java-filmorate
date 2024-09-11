package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.DeleteStorage;
import ru.yandex.practicum.filmorate.repository.ModelRepository;
import ru.yandex.practicum.filmorate.repository.ReviewLikeStorage;

import java.util.Collection;

@Service
public class ReviewService extends ModelService<Review> {

    private final ReviewLikeStorage reviewLikeStorage;
    private final ModelRepository<User> userModelRepository;
    private final ModelRepository<Film> filmModelRepository;
    private final EventFeedService eventFeedService;

    private final DeleteStorage deleteStorage;

    public ReviewService(ModelRepository<Review> reviewModelRepository, ModelRepository<User> userModelRepository,
                         ModelRepository<Film> filmModelRepository, EventFeedService eventFeedService) {
        super(reviewModelRepository);
        this.reviewLikeStorage = (ReviewLikeStorage) reviewModelRepository;
        this.userModelRepository = userModelRepository;
        this.filmModelRepository = filmModelRepository;
        deleteStorage = (DeleteStorage) reviewModelRepository;
        this.eventFeedService = eventFeedService;
    }

    public boolean checkDataReview(Review review) {
        final User user = userModelRepository.getById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with " + review.getUserId()));
        final Film film = filmModelRepository.getById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Film not found with " + review.getFilmId()));
        return true;
    }

    public int addLike(long id, long userId) {
        final Review review = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Review not found with " + id));
        final User user = userModelRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        reviewLikeStorage.addLike(review, user);
        return review.getUseful();
    }

    public int addDisLike(long id, long userId) {
        final Review review = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Review not found with " + id));
        final User user = userModelRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        reviewLikeStorage.addDisLike(review, user);
        return review.getUseful();
    }

    public int deleteLike(long id, long userId) {
        final Review review = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Review not found with " + id));
        final User user = userModelRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        reviewLikeStorage.deleteLike(review, user);
        return review.getUseful();
    }

    public Collection<Review> getReviews(long filmId, Integer count) {
        return reviewLikeStorage.getCountReview(filmId, count);
    }

    public Review get(long id) {
        return repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
    }

   /* public Review create(Review model) {
        Review review = repository.create(model);
        eventFeedService.addEvent(model.getUserId(), EventType.REVIEW, EventOperation.ADD, model.getReviewId());
        return review;
    }*/
   public Review create(Review model) {
       Review  review = null;
       if(model.getContent() == null) {
           throw new ValidationException("Content должен быть указан.");
       }
       if(model.getIsPositive() == null) {
           throw new ValidationException("IsPositive должен быть указан.");
       }
       if (checkDataReview(model)) {
           review = repository.create(model);
       }
       eventFeedService.addEvent(model.getUserId(), EventType.REVIEW, EventOperation.ADD, model.getReviewId());
       return review;
   }

    public Review update(Review model) {
        Review review = repository.update(model);
        eventFeedService.addEvent(model.getUserId(), EventType.REVIEW, EventOperation.UPDATE, model.getReviewId());
        return review;
    }

    public void delete(long id) {
        Review review = repository.getById(id)
                        .orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
        deleteStorage.delete(id);
        eventFeedService.addEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());
    }
}

