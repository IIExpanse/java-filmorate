package ru.yandex.practicum.filmorate.dao.mpa;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.mpa.MPANotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("MpaDAO")
@Primary
@AllArgsConstructor
public class MpaDAO {

    private final JdbcTemplate template;

    public MPA getMPA(int id) {
        MPA mpa;
        try {
            mpa = template.queryForObject("SELECT * FROM \"mpa_rating\" WHERE \"mpa_id\" = " + id, new MPAMapper());
        } catch (DataAccessException e) {
            throw new MPANotFoundException("Ошибка получения рейтинга: не найден возрастной рейтинг с id=" + id);
        }

        return mpa;
    }

    public List<MPA> getMPAs() {
        return template.query("SELECT * FROM \"mpa_rating\"", new MPAMapper());
    }

    static class MPAMapper implements RowMapper<MPA> {
        @Override
        public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MPA(rs.getInt("mpa_id"), rs.getString("mpa_name"));
        }
    }
}
