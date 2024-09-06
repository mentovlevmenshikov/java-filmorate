package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

@Service
public class ValidationDirectorService implements ValidationService<Director> {
    @Override
    public void validate4Create(Director director) {
        validateName(director);
    }

    @Override
    public void validate4Update(Director director) {
        validateId(director);
        validate4Create(director);
    }

    private void validateId(Director director) {
        if (director.getId() == null) {
            throw new ValidationException("Id должен быть указан.");
        }
    }

    private void validateName(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
    }
}
