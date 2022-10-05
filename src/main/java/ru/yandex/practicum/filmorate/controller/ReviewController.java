package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final FilmService filmService;
    private final UserService userService;
    
    @Autowired
    public ReviewController(@Qualifier("FilmDBService") FilmService filmService,
                            @Qualifier("UserDBService") UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }
    
    /**
     * Посмотреть все отзывы о фильме.
     * @param filmId ID фильма.
     */
    public ResponseEntity<?> getReviewsByFilmId(Integer filmId) {
        return null;
    }
    
    /**
     * Посмотреть отзыв о фильме определённого пользователя.
     * @param filmId ID фильма.
     * @param userid ID пользователя.
     * @return
     */
    public ResponseEntity<?> getReviewByFilmIdAndUserId(Integer filmId, Integer userid) {
        return null;
    }
    
    /**
     * Посмотреть все отзывы пользователя.
     * @param userId ID пользователя.
     * @return
     */
    public ResponseEntity<?> getReviewsByUserId(Integer userId) {
        return null;
    }
    
}
