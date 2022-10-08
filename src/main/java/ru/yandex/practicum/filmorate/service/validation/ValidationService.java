package ru.yandex.practicum.filmorate.service.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;

@Service
@Slf4j
public class ValidationService {
    public void checkReview(Review review) {
        final String content = review.getContent();
        final Integer filmId = review.getFilmId();
        final Integer userId = review.getUserId();
        final Object isPositive = review.getIsPositive();
        
        if (content == null || content.isBlank()) {
            String error = "Содержимое отзыва пусто.";
            throw new RuntimeException(error);
        }
        
        if (filmId == null) {
            String error = "В отзыве 'filmId' = null.";
            throw new RuntimeException(error);
        }
    
        if (userId == null) {
            String error = "В отзыве 'userId' = null.";
            throw new RuntimeException(error);
        }
    
        if (review.getIsPositive() == null) {
            String error = "В отзыве 'filmId' = null.";
            throw new RuntimeException(error);
        }
    
    }
}
