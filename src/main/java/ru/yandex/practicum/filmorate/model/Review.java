package ru.yandex.practicum.filmorate.model;

import lombok.*;

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
    private final Integer id;
    
    @NotBlank(message = "Отзыв о фильме не должен быть пустым.")
    private final String content;
    
    @NonNull()
    private final boolean isPositive;
    
    @NotNull(message = "В отзыве ID фильма не должно быть пустым.")
    private final Integer filmId;
    
    @NotNull(message = "В отзыве ID пользователя, написавшего отзыв, не должно быть пустым.")
    private final Integer userId;
}
