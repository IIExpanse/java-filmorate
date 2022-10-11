package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Genre {

    @Positive(message = "Id жанра должно быть больше нуля.")
    private final int id;
    @NotBlank(message = "Название жанра не может быть пустым")
    private final String name;
}