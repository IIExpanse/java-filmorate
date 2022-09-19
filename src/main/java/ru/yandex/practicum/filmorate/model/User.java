package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.friend.FriendAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.validator.NoWhitespaceConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    private int id;
    @NotBlank
    @Email
    private final String email;
    @NotBlank
    @NoWhitespaceConstraint
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;
    @JsonIgnore
    private final Set<Integer> friendsIds = new HashSet<>();

    public void addFriend(int friendId) {

        if (!friendsIds.add(friendId)) {
            throw new FriendAlreadyAddedException(
                    String.format("Ошибка при добавлении друга для пользователя с id=%d: " +
                            "друг с id=%d уже добавлен.", id, friendId)
            );
        }
    }

    public void removeFriend(int friendId) {
        if (!friendsIds.remove(friendId)) {
            throw new FriendNotFoundException(
                    String.format("Ошибка при удалении друга у пользователя с id=%d: " +
                            "друг с id=%d не найден.", id, friendId)
            );
        }
    }
}
