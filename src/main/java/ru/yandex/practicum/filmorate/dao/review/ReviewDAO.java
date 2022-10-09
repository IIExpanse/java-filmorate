package ru.yandex.practicum.filmorate.dao.review;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ReviewDAO")
@AllArgsConstructor
public class ReviewDAO {
    private final JdbcTemplate jdbcTemplate;
    
    // TODO: 2022.10.09 04:41:51 Удалить этот класс или сюда переместить из другой пакпи ReviewUserReact
    //  - @Dmitriy_Gaju
}
