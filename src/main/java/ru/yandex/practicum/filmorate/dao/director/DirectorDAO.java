package ru.yandex.practicum.filmorate.dao.director;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.director.DirectorAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository("DirectorDAO")
@Primary
@AllArgsConstructor
public class DirectorDAO {

    private final JdbcTemplate template;

    public Director getDirector(int id) {
        Director director;
        try {
            director = template.queryForObject("SELECT * FROM \"directors\" WHERE \"director_id\" = " + id,
                    new DirectorMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException(String.format("Ошибка: режиссер с id=%d не найден", id));
        }
        return director;
    }

    public Collection<Director> getDirectors() {
        return template.query("SELECT * FROM \"directors\"", new DirectorMapper());
    }

    public int addDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Number id;

        try {
            template.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO \"directors\" (\"director_name\")" +
                                "VALUES (?)", Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, director.getName());
                return ps;
            }, keyHolder);

        } catch (DuplicateKeyException e) {
            throw new DirectorAlreadyAddedException(
                    String.format("Ошибка при добавлении режиссера: режиссер с именем %s уже добавлен",
                            director.getName())
            );
        }

        id = keyHolder.getKey();
        if (id == null) {
            throw new RuntimeException("Ошибка: режиссер не был добавлен.");
        }
        return id.intValue();
    }

    public void pairFilmsWithDirectors(int filmId, List<Integer> directorsIds) {
        List<Object[]> list = new ArrayList<>();

        for (int directorId : directorsIds) {
            Object[] arr = {filmId, directorId};
            list.add(arr);
        }
        template.batchUpdate("MERGE INTO \"film_directors\" (\"film_id\", \"director_id\")" +
                "VALUES (?, ?)", list);
    }

    public void updateDirector(Director director) {
        int id = director.getId();

        int affected = template.update("UPDATE \"directors\" SET \"director_name\" = ? WHERE \"director_id\" = ?",
                director.getName(), id);

        if (affected == 0) {
            throw new DirectorNotFoundException(String.format("Ошибка при обновлении: режиссер с id=%d не найден", id));
        }
    }

    public void removeDirector(int id) {
        int affected = template.update("DELETE FROM \"directors\" WHERE \"director_id\" = ?", id);

        if (affected == 0) {
            throw new DirectorNotFoundException(String.format("Ошибка при удалении: режиссер с id=%d не найден", id));
        }
    }

    static class DirectorMapper implements RowMapper<Director> {
        @Override
        public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Director(rs.getInt("director_id"), rs.getString("director_name"));
        }
    }
}
