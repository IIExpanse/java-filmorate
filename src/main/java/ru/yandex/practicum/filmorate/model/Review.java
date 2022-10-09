package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Review {
    @Positive
    private int reviewId;
    @NotBlank
    private final String content;
    private final boolean isPositive;
    @Positive
    private final int userId;
    @NotNull
    private final int filmId;
    private int useful;
}
