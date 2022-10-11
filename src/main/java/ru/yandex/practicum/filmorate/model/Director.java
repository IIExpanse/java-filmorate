package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class Director {
    @Positive(message = "Id режиссера должно быть больше нуля.")
    private int id;
    @NotBlank(message = "Имя режиссера не может быть пустым.")
    private final String name;
}
