package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

@Service
public class ValidationReview implements ValidationService<Review> {
    @Override
    public void validate4Create(Review review) {
        validateСontent(review);
    }

    @Override
    public void validate4Update(Review review) {
        validateId(review);
        validate4Create(review);
    }

    private void validateId(Review review) {
        if (review.getReviewId() == null) {
            throw new ValidationException("Id должен быть указан.");
        }
    }

    private void validateСontent(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
    }

}
