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
public class MPA {
    @Positive(message = "Id возрастного рейтинга должно быть больше нуля.")
    private final int id;
    @NotBlank(message = "Название возрастного рейтинга не может быть пустым.")
    private final String name;
}