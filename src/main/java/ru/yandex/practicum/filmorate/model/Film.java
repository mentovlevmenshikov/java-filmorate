package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.validator.RealiseDateConstraint;
import ru.yandex.practicum.filmorate.model.validator.Update;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Film.
 */

@Builder
@Data
public class Film implements Model {
    @NotNull(groups = Update.class)
    private Long id;
    @NotBlank(message = "Название не может быть пустым (анотация)")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания 200")
    private String description;
    @NotNull
    @RealiseDateConstraint()
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом (анотация)")
    private Long duration;
    @NotNull
    private MPA mpa;
    @Builder.Default
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private int countLikes;
    @Builder.Default
    private HashSet<Director> directors = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
