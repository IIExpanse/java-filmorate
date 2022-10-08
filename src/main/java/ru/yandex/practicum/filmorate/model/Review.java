package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//{
//  "content": "This film is soo bad.",
//  "isPositive": false,
//  "userId": 1,
//  "filmId": 1
//}

@EqualsAndHashCode(onlyExplicitlyIncluded = true)           //только явным образом включённые
@RequiredArgsConstructor
@Getter
@Setter
public class Review {
    @EqualsAndHashCode.Include                              //поле включено в EqualsHashCode
    @NotNull(message = "ID для отзыва о фильме не должно быть пустым.")
    private Integer id;
    
    @NotBlank(message = "Отзыв о фильме не должен быть пустым.")
    private final String content;
    
    @NotNull(message = "Отзыв о фильме должен быть положительным или отрицательным. Поле 'isPositive' = null.")
    private final Boolean isPositive;
    
    @NotNull(message = "В отзыве ID фильма не должно быть пустым.")
    private final Integer filmId;
    
    @NotNull(message = "В отзыве ID пользователя, написавшего отзыв, не должно быть пустым.")
    private final Integer userId;
    
    /**
     * Карта оценок отзыва пользователями.
     */
    private Integer useful;
}
