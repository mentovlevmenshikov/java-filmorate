package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationFilmServiceTest {

    private Film film;

    @BeforeEach
    void setUp() {

        film = Film.builder()
                .id(1L)
                .name("Название фильма")
                .description("описание")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .build();
    }

    private final ValidationService<Film> validationService = new ValidationFilmService();

    @Test
    @DisplayName("Проверка названия фильма")
    void validateName() {
        film.setName(null);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(film),
                "Название фильма не может быть null");

        film.setName("   ");

        assertThrows(ValidationException.class, () -> validationService.validate4Update(film),
                "Название фильма не может состоять из одних пробелов");

        film.setName("Хорошее название");
        assertDoesNotThrow(() -> validationService.validate4Update(film));
    }

    @Test
    @DisplayName("Проверка описания фильма")
    void validateMaxLenDescription() {
        String description = "-".repeat(201);
        film.setDescription(description);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(film),
                "Описание не может быть длиньше 200 символов");

        description = "-".repeat(200);
        film.setDescription(description);
        assertDoesNotThrow(() -> validationService.validate4Update(film));

        film.setDescription(null);
        assertDoesNotThrow(() -> validationService.validate4Update(film), "Описание может быть null");
    }

    @Test
    @DisplayName("Проверка даты релиза")
    void validateReleaseDate() {
        LocalDate releasDate = LocalDate.of(1895, 11, 28);
        film.setReleaseDate(releasDate);
        assertDoesNotThrow(() -> validationService.validate4Update(film), "Дата релиза может быть равна минимальной дате");

        releasDate = releasDate.minusDays(1);
        film.setReleaseDate(releasDate);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(film), "Дата релиза не может быть меньше 28.11.1985");

        film.setReleaseDate(null);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(film), "Дата релиза не может быть равна null");
    }

    @Test
    @DisplayName("Проверка продолжительности фильма")
    void validateDuration() {
        film.setDuration(-30L);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(film), "Продолжительность должна быть больше 0");

        film.setDuration(0L);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(film), "Продолжительность должна быть больше 0");

        film.setDuration(null);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(film), "Продолжительность не может быть null");

        film.setDuration(1L);
        assertDoesNotThrow(() -> validationService.validate4Update(film), "Продолжительность верная");
    }

    @Test
    void validateId() {
        film.setId(null);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(film), "Id фильма не может быть null для обновления");

        assertDoesNotThrow(() -> validationService.validate4Create(film), "Id фильма может быть null для создания");
    }
}