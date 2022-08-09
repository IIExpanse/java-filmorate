package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.NoWhitespaceConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private int id;
    @Email
    private final String email;
    @NotBlank
    @NoWhitespaceConstraint
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;

    @JsonCreator
    public User(
            @JsonProperty("email") String email,
            @JsonProperty("login") String login,
            @JsonProperty("name") String name,
            @JsonProperty("birthday") LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    @JsonCreator
    public User(
            @JsonProperty("id") int id,
            @JsonProperty("email") String email,
            @JsonProperty("login") String login,
            @JsonProperty("name") String name,
            @JsonProperty("birthday") LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
