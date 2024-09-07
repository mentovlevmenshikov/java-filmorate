package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface ReviewLikeStorage {
    void addLike(Review review, User user);

    void addDisLike(Review review, User user);

    void deleteLike(Review review, User user);

    Collection<Review> getCountReview(long filmId,int count);
}
