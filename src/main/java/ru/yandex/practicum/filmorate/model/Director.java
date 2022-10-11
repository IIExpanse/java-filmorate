package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Director {
    @Positive
    private int id;
    @NotBlank
    private final String name;
}
