package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.validator.RealiseDateConstraint;
import ru.yandex.practicum.filmorate.model.validator.Update;

import java.time.LocalDate;

/**
 * Film.
 */

@Data
public class Film {
    @NotNull(groups = Update.class)
    private Long id;
    @NotBlank(message = "Название не может быть пустым (анотация)")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания 200")
    private String description;
    @RealiseDateConstraint()
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом (анотация)")
    private Long duration;
}
