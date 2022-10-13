package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.director.DirectorAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.friend.CantAddSelfException;
import ru.yandex.practicum.filmorate.exception.friend.FriendAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.like.LikeAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.mpa.MPANotFoundException;
import ru.yandex.practicum.filmorate.exception.review.ReviewAlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            FilmNotFoundException.class,
            FriendNotFoundException.class,
            LikeNotFoundException.class,
            UserNotFoundException.class,
            GenreNotFoundException.class,
            MPANotFoundException.class,
            ReviewNotFoundException.class,
            MPANotFoundException.class,
            DirectorNotFoundException.class
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
            DirectorAlreadyAddedException.class,
            CantAddSelfException.class,
            DataIntegrityViolationException.class,
            ReviewAlreadyLikedException.class
    })
    public ResponseEntity<ErrorResponse> handleConflictExceptions(final RuntimeException e) {
        String exceptionName = e.getClass().getName();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf("."));
        log.debug(e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(exceptionName, e.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleIllegalInputExceptions(final Exception e) {
        String exceptionName = e.getClass().getName();
        String exceptionMessage = e.getMessage();
        exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1);

        if (e instanceof MethodArgumentNotValidException) {
            int start = exceptionMessage.lastIndexOf("[") + 1;
            exceptionMessage = e.getMessage().substring(start, exceptionMessage.indexOf("]", start));
        }

        log.debug(e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(exceptionName, exceptionMessage),
                HttpStatus.BAD_REQUEST
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
