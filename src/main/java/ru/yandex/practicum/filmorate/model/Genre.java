package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.validator.Update;

@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Genre {
    @NotNull(groups = Update.class)
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
}
