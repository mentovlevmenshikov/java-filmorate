package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.ModelRepository;
import ru.yandex.practicum.filmorate.repository.ReviewLikeStorage;

import java.util.Collection;

@Service
public class ReviewService extends ModelService<Review> {

    private final ReviewLikeStorage reviewLikeStorage;
    private final ModelRepository<User> userModelRepository;
    private final ModelRepository<Film> filmModelRepository;
    public ReviewService(ModelRepository<Review> reviewModelRepository,ModelRepository<User> userModelRepository,ModelRepository<Film> filmModelRepository) {
        super(reviewModelRepository);
        this.reviewLikeStorage = (ReviewLikeStorage)reviewModelRepository;
        this.userModelRepository = userModelRepository;
        this.filmModelRepository = filmModelRepository;
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

    public int deleteLike(long id, long userId) {
        final Review review  = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Review not found with " + id));
        final User user = userModelRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
        reviewLikeStorage.deleteLike(review, user);
        return review.getUseful();
    }

    public Collection<Review> getReviews(long filmId,Integer count) {
        return reviewLikeStorage.getCountReview(filmId,count);
    }
    public Review get(long id) {
        return repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Сущность с id " + id + " не найдена"));
    }
    public Review create(Review model) {
        return repository.create(model);
    }

    @Override
    public Review update(Review model) {
        return repository.update(model);
    }
}
