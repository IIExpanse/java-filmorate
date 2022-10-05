package ru.yandex.practicum.filmorate.dao.review;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ReviewDAO")
@AllArgsConstructor
public class ReviewDAO {
    private final JdbcTemplate jdbcTemplate;
    
    
}
