package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

//{
//  "content": "This film is soo bad.",
//  "isPositive": false,
//  "userId": 1,
//  "filmId": 1
//}

@EqualsAndHashCode(onlyExplicitlyIncluded = true)           //только явным образом включённые
@AllArgsConstructor
//@NoArgsConstructor
@Getter
@Setter
public class Review {
    @EqualsAndHashCode.Include                              //поле включено в EqualsHashCode
//    @NotNull(message = "ID для отзыва о фильме не должно быть пустым.")
    private Integer reviewId;
    
    //    @NotBlank(message = "Отзыв о фильме не должен быть пустым.")
    private final String content;
    
    @NotNull//(message = "Отзыв о фильме должен быть положительным или отрицательным. Поле 'isPositive' = null.")
    private final Boolean isPositive;
    
    @NotNull//(message = "В отзыве ID фильма не должно быть пустым.")
    private final Integer filmId;
    
    @NotNull//(message = "В отзыве ID пользователя, написавшего отзыв, не должно быть пустым.")
    private final Integer userId;
    
    /**
     * Карта оценок отзыва пользователями.
     */
    @NotNull(message = "Значение по умолчанию для полезности отзыва = 0.")
    private Integer useful;
}
