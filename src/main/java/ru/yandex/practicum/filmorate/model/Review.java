package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = "reviewId")
public class Review {
    Long reviewId;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    int useful;
}

