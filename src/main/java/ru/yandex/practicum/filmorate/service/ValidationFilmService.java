package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Service
public class ValidationFilmService implements ValidationService<Film> {
    @Override
    public void validate4Create(Film film) {
        validateName(film);
        validateDescription(film);
        validateReleaseDate(film);
        validateDuration(film);
    }

    @Override
    public void validate4Update(Film film) {
        validateId(film);
        validate4Create(film);
    }

    private void validateId(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан.");
        }
    }

    private void validateName(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
    }

    private void validateDescription(Film film) {
        if (film.getDescription() == null) {
            return;
        }

        int maxLenDescription = 200;
        if (film.getDescription().length() > maxLenDescription) {
            throw new ValidationException(String.format("Максимальная длина описания %s, а текущая %s", maxLenDescription,
                    film.getDescription().length()));
        }
    }

    private void validateReleaseDate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate == null) {
            throw new ValidationException("Не указана дата релиза.");
        }

        LocalDate minReleasDate = LocalDate.of(1895, 11, 28);
        if (minReleasDate.isAfter(releaseDate)) {
            throw new ValidationException(String.format("Дата релиза должна быть не раньше %s", minReleasDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
        }
    }

    private void validateDuration(Film film) {
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw  new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}
