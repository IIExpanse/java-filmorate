package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Review {
    private int reviewId;
    @NotBlank
    @NotNull
    private final String content;
    @NotNull
    private final Boolean isPositive;
    @NotNull
    private final Integer filmId;
    @NotNull
    private final Integer userId;
    private int useful;

    @JsonProperty("isPositive")
    public boolean isPositive() {
        return isPositive;
    }
}