package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)           //только явным образом включённые
@RequiredArgsConstructor
@Getter
@Setter
public class UserLikedReview {
    @NotNull(message = "ID пользователя, поставившего лайк отзыву, должно быть не 'null'.")
    private final Integer userId;
    @NotNull(message = "Оценка отзыва должна быть не 'null'.")
    private final Boolean isLike;
}
