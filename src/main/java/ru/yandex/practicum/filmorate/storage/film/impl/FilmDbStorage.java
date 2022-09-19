package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.genre.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.like.LikeAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.mpa.MPANotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository("FilmDbStorage")
@Primary
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate template;

    @Override
    public Film getFilm(int id) {
        SqlRowSet rowSet = template.queryForRowSet("SELECT * FROM \"films\" WHERE \"film_id\" = ?", id);
        if (!rowSet.next()) {
            throw new FilmNotFoundException(String.format("Ошибка получения: фильм с id=%d не найден.", id));
        }

        int mpaId = rowSet.getInt("mpa_id");
        Film film = new Film(id,
                rowSet.getString("name"),
                rowSet.getString("description"),
                rowSet.getDate("release_date").toLocalDate(),
                rowSet.getInt("duration"),
                rowSet.getInt("rate"),
                new MPA(mpaId,
                        template.queryForObject(
                                "SELECT \"name\" FROM \"mpa_rating\" WHERE \"mpa_id\" = " + mpaId, String.class)));

        addLikesToDTO(film, id);
        addGenresToDTO(film, id);

        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        SqlRowSet rowSet = template.queryForRowSet("SELECT * FROM \"films\"");
        List<Film> list = new ArrayList<>();

        while (rowSet.next()) {
            int mpaId = rowSet.getInt("mpa_id");

            Film film = new Film(
                    rowSet.getInt("film_id"),
                    rowSet.getString("name"),
                    rowSet.getString("description"),
                    rowSet.getDate("release_date").toLocalDate(),
                    rowSet.getInt("duration"),
                    rowSet.getInt("rate"),
                    new MPA(mpaId,
                            template.queryForObject(
                                    "SELECT \"name\" FROM \"mpa_rating\" WHERE \"mpa_id\" = " + mpaId, String.class))
            );
            addLikesToDTO(film, film.getId());
            addGenresToDTO(film, film.getId());
            list.add(film);
        }

        return list;
    }

    @Override
    public Genre getGenre(int id) {
        SqlRowSet genreRow = template.queryForRowSet("SELECT * FROM \"genres\" WHERE \"genre_id\" = ?", id);
        if (!genreRow.next()) {
            throw new GenreNotFoundException("Ошибка получения жанра: не найден жанр с id=" + id);
        }

        return new Genre(genreRow.getInt("genre_id"), genreRow.getString("name"));
    }

    @Override
    public List<Genre> getGenres() {
        SqlRowSet genreRows = template.queryForRowSet("SELECT * FROM \"genres\"");
        List<Genre> list = new ArrayList<>();

        while (genreRows.next()) {
            list.add(new Genre(genreRows.getInt("genre_id"), genreRows.getString("name")));
        }
        return list;
    }

    @Override
    public MPA getMPA(int id) {
        SqlRowSet mpaRow = template.queryForRowSet("SELECT * FROM \"mpa_rating\" WHERE \"mpa_id\" = ?", id);
        if (!mpaRow.next()) {
            throw new MPANotFoundException("Ошибка получения рейтинга: не найден возрастной рейтинг с id=" + id);
        }

        return new MPA(mpaRow.getInt("mpa_id"), mpaRow.getString("name"));
    }

    public List<MPA> getMPAs() {
        SqlRowSet mpaRows = template.queryForRowSet("SELECT * FROM \"mpa_rating\"");
        List<MPA> list = new ArrayList<>();

        while (mpaRows.next()) {
            list.add(new MPA(mpaRows.getInt("mpa_id"), mpaRows.getString("name")));
        }
        return list;
    }

    @Override
    public int addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Number id;

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"films\" (\"name\", " +
                            "\"description\", " +
                            "\"release_date\", " +
                            "\"duration\", " +
                            "\"rate\"," +
                            "\"mpa_id\") VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRate());
            ps.setInt(6, film.getMpa().getId());
            return ps;
        }, keyHolder);

        id = keyHolder.getKey();
        if (id == null) {
            throw new RuntimeException("Ошибка: фильм не был добавлен.");
        }

        insertGenres(film, id.intValue());
        return id.intValue();
    }

    @Override
    public void addLike(int targetFilmId, int userId) {
        Integer responseId = getIdFromDB(targetFilmId);
        if (responseId == null) {
            throw new FilmNotFoundException(
                    String.format("Ошибка при добавлении лайка: фильм с id=%d не найден.", targetFilmId));
        }

        try {
            template.update("INSERT INTO \"likes\" (\"film_id\", \"from_user_id\") " +
                    "VALUES (?, ?)", targetFilmId, userId);
        } catch (DuplicateKeyException e) {
            throw new LikeAlreadyAddedException(
                    String.format("Ошибка при добавлении лайка для фильма с id=%d " +
                            "от пользователя с id=%d: лайк уже добавлен.", userId, targetFilmId));

        } catch (DataIntegrityViolationException e) {
            throw new UserNotFoundException(
            String.format("Ошибка при добавлении лайка для фильма с id=%d " +
                    "от пользователя с id=%d: пользователь не найден.", userId, targetFilmId));
        }
    }

    @Override
    public void updateFilm(Film film, int id) {
        Integer responseId = getIdFromDB(id);

        if (responseId == null) {
            throw new FilmNotFoundException(String.format("Ошибка обновления: фильм с id=%d не найден.", id));
        }

        template.update("UPDATE \"films\" SET \"name\" = ?," +
                        "\"description\" = ?," +
                        "\"release_date\" = ?," +
                        "\"duration\" = ?," +
                        "\"rate\" = ?," +
                        "\"mpa_id\" = ?" +
                        "WHERE \"film_id\" = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                id);

        template.update("DELETE FROM \"film_genres\" WHERE \"film_id\" = ?", id);
        insertGenres(film, id);
    }

    @Override
    public void removeLike(int targetFilmId, int userId) {
        Integer responseId = getIdFromDB(targetFilmId);
        if (responseId == null) {
            throw new FilmNotFoundException(
                    String.format("Ошибка при удалении лайка: фильм с id=%d не найден.", targetFilmId));
        }

        try {
            template.queryForObject("SELECT \"film_id\" FROM \"likes\" " +
                            "WHERE \"film_id\" = " + targetFilmId + " AND \"from_user_id\" = " + userId,
                    Integer.class);

        } catch (EmptyResultDataAccessException e) {
            throw new LikeNotFoundException(
                    String.format("Ошибка при удалении лайка: " +
                            "лайк к фильму с id=%d от пользователя с id=%d не найден.", targetFilmId, userId));
        }

        template.update("DELETE FROM \"likes\" WHERE \"film_id\" = ? AND \"from_user_id\" = ?",
                targetFilmId, userId);
    }

    private Integer getIdFromDB(int id) {
        Integer result;
        try {
            result = template.queryForObject("SELECT \"film_id\" FROM \"films\" WHERE \"film_id\" = " + id,
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    private void addLikesToDTO(Film film, int id) {
        SqlRowSet rowSet = template.queryForRowSet("SELECT \"from_user_id\" FROM \"likes\" WHERE \"film_id\" = ?", id);

        while (rowSet.next()) {
            film.addLike(rowSet.getInt("from_user_id"));
        }
    }

    private void addGenresToDTO(Film film, int id) {
        SqlRowSet rowSet = template.queryForRowSet("SELECT * FROM \"film_genres\" WHERE \"film_id\" = ? " +
                "ORDER BY \"genre_id\"", id);
        while (rowSet.next()) {
            film.addGenre(getGenre(rowSet.getInt("genre_id")));
        }
    }

    private void insertGenres(Film film, int id) {
        try {
            for (Genre genre : film.getGenres()) {
                template.update("MERGE INTO \"film_genres\" (\"film_id\", \"genre_id\")" +
                        "VALUES (?, ?)", id, genre.getId());
            }
        } catch (DataIntegrityViolationException e) {
            throw new GenreNotFoundException("Ошибка при добавлении фильма: " +
                    "один или несколько идентификаторов жанров не найдены.");
        }
    }
}
