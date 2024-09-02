package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.validator.Update;

@Builder
@Data
public class MPA {
    @NotNull(groups = Update.class)
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
}
