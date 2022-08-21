package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.yandex.practicum.filmorate.exception.like.LikeAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {

    private int id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    @ReleaseDateConstraint(year = 1895, month = 12, day = 28)
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int rate;
    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

    public void addLike(int userId) {
        if (!likes.add(userId)) {
            throw new LikeAlreadyAddedException(
                    String.format("Ошибка при добавлении лайка к фильму с id=%d: " +
                            "пользователь с id=%d уже поставил фильму лайк.", id, userId)
            );
        }
        updateRate();
    }

    public void removeLike(int userId) {
        if (!likes.remove(userId)) {
            throw new LikeNotFoundException(
                    String.format("Ошибка при удалении лайка у фильма с id=%d: " +
                            "лайк пользователя с id=%d не найден.", id, userId));
        }
        updateRate();
    }

    private void updateRate() {
        this.rate = likes.size();
    }
}
