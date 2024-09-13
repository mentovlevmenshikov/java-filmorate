package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.validator.Update;

@Builder
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    @NotNull(groups = Update.class)
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 100, message = "Максимальная длина имени - 100 символов.")
    private String name;
}
