package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.films.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.friends.CantAddSelfException;
import ru.yandex.practicum.filmorate.exceptions.friends.FriendAlreadyAddedException;
import ru.yandex.practicum.filmorate.exceptions.friends.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.likes.LikeAlreadyAddedException;
import ru.yandex.practicum.filmorate.exceptions.likes.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.users.UserNotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            FilmNotFoundException.class,
            FriendNotFoundException.class,
            LikeNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(final RuntimeException e) {
        String exceptionName = e.getClass().getName();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1);
        log.debug(e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(exceptionName, e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({
            FriendAlreadyAddedException.class,
            LikeAlreadyAddedException.class,
            CantAddSelfException.class
    })
    public ResponseEntity<ErrorResponse> handleAlreadyAdded(final RuntimeException e) {
        String exceptionName = e.getClass().getName();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf("."));
        log.debug(e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(exceptionName, e.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @Getter
    static class ErrorResponse {

        private final String errorName;
        private final String errorMessage;

        public ErrorResponse(String errorName, String errorMessage) {
            this.errorName = errorName;
            this.errorMessage = errorMessage;
        }
    }
}
