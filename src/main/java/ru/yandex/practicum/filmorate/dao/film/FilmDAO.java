package ru.yandex.practicum.filmorate.dao.film;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.director.DirectorDAO;
import ru.yandex.practicum.filmorate.dao.feed.FeedDAO;
import ru.yandex.practicum.filmorate.dao.genre.GenreDAO;
import ru.yandex.practicum.filmorate.dao.mpa.MpaDAO;
import ru.yandex.practicum.filmorate.exception.director.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.like.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.SearchBy;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository("FilmDAO")
@Primary
@AllArgsConstructor
public class FilmDAO implements FilmStorage {

    private final JdbcTemplate template;
    private final GenreDAO genreDAO;
    private final MpaDAO mpaDAO;
    private final DirectorDAO directorDAO;
    private final FeedDAO feed;

    @Override
    public Film getFilm(int id) {
        Film film;
        try {
            film = template.queryForObject("SELECT * FROM \"films\"" +
                            "JOIN \"mpa_rating\" mr ON \"films\".\"mpa_id\" = mr.\"mpa_id\" " +
                            "WHERE \"film_id\" = " + id,
                    new FilmMapper());
        } catch (DataAccessException e) {
            throw new FilmNotFoundException(String.format("Ошибка получения: фильм с id=%d не найден.", id));
        }

        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        return template.query("SELECT * FROM \"films\" " +
                "JOIN \"mpa_rating\" mr ON \"films\".\"mpa_id\" = mr.\"mpa_id\"", new FilmMapper());
    }

    @Override
    public Collection<Film> getDirectorFilms(int id) {
        try {
            template.queryForObject("SELECT \"director_id\" " +
                    "FROM \"directors\" WHERE \"director_id\" = " + id, Integer.class);
        } catch (DataAccessException e) {
            throw new DirectorNotFoundException(String.format("Режиссер с id=%d не найден.", id));
        }

        return template.query("SELECT * FROM \"films\"" +
                "JOIN \"mpa_rating\" mr ON \"films\".\"mpa_id\" = mr.\"mpa_id\"" +
                "WHERE \"film_id\" IN (" +
                    "SELECT \"film_id\" " +
                    "FROM \"film_directors\"" +
                    "WHERE \"director_id\" = " + id + ")", new FilmMapper());
    }

    @Override
    public Collection<Film> getCommonFilms(int firstUserId, int secondUserId) {
        String sql = "SELECT \"film_id\" FROM \"likes\" WHERE \"from_user_id\" = ?";

        List<Integer> likedFilms1 = template.query(sql, (rs, rowNum) -> rs.getInt("film_id"), firstUserId);
        List<Integer> likedFilms2 = template.query(sql, (rs, rowNum) -> rs.getInt("film_id"), secondUserId);
        String[] sharedFilms = likedFilms1.stream()
                .filter(likedFilms2::contains)
                .map(String::valueOf)
                .toArray(String[]::new);
        String argsList = String.join(",", sharedFilms);

        sql = String.format("SELECT * FROM \"films\" " +
                "JOIN \"mpa_rating\" mr ON \"films\".\"mpa_id\" = mr.\"mpa_id\" " +
                "WHERE \"film_id\" IN (%s)", argsList);

        return template.query(sql, new FilmMapper());
    }

    @Override
    public Genre getGenre(int id) {
        return genreDAO.getGenre(id);
    }

    @Override
    public List<Genre> getGenres() {
        return genreDAO.getGenres();
    }

    @Override
    public MPA getMPA(int id) {
        return mpaDAO.getMPA(id);
    }

    @Override
    public List<MPA> getMPAs() {
        return mpaDAO.getMPAs();
    }

    @Override
    public Director getDirector(int id) {
        return directorDAO.getDirector(id);
    }

    @Override
    public Collection<Director> getDirectors() {
        return directorDAO.getDirectors();
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Number id;

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"films\" (" +
                            "\"film_name\", " +
                            "\"description\", " +
                            "\"release_date\", " +
                            "\"duration\", " +
                            "\"rate\"," +
                            "\"mpa_id\") " +
                            "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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

        genreDAO.insertGenres(film, id.intValue());
        directorDAO.pairFilmsWithDirectors(id.intValue(),
                film.getDirectors().stream()
                        .map(Director::getId)
                        .collect(Collectors.toList()));
        return getFilm(id.intValue());
    }

    @Override
    public void addLike(int targetFilmId, int userId) {
        Integer responseId = getIdFromDB(targetFilmId);
        if (responseId == null) {
            throw new FilmNotFoundException(
                    String.format("Ошибка при добавлении лайка: фильм с id=%d не найден.", targetFilmId));
        }

        try {
            template.update("MERGE INTO \"likes\" (\"film_id\", \"from_user_id\") " +
                    "VALUES (?, ?)", targetFilmId, userId);
            feed.addLikeEvent(userId, targetFilmId);

        } catch (DataIntegrityViolationException e) {
            throw new UserNotFoundException(
                    String.format("Ошибка при добавлении лайка для фильма с id=%d " +
                            "от пользователя с id=%d: пользователь не найден.", userId, targetFilmId));
        }
    }

    public Director addDirector(Director director) {
        return directorDAO.addDirector(director);
    }

    @Override
    public Film updateFilm(Film film, int id) {
        Integer responseId = getIdFromDB(id);

        if (responseId == null) {
            throw new FilmNotFoundException(String.format("Ошибка обновления: фильм с id=%d не найден.", id));
        }

        template.update("UPDATE \"films\" SET " +
                        "\"film_name\" = ?," +
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
        template.update("DELETE FROM \"film_directors\" Where \"film_id\" = ?", id);
        genreDAO.insertGenres(film, id);
        directorDAO.pairFilmsWithDirectors(id,
                film.getDirectors().stream()
                        .map(Director::getId)
                        .collect(Collectors.toList()));
        return getFilm(film.getId());
    }

    @Override
    public void removeDirector(int id) {
        directorDAO.removeDirector(id);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorDAO.updateDirector(director);
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
        feed.removeLikeEvent(userId, targetFilmId);
    }

    @Override
    public Collection<Film> getFilmRecommendation(int userId) {
        String sqlQuery = "SELECT * FROM \"films\" as f" +
                " LEFT JOIN \"mpa_rating\" as mr ON f.\"mpa_id\" = mr.\"mpa_id\"" +
                " WHERE f.\"film_id\" in (select \"film_id\" from \"likes\"" +
        " where \"film_id\" in (select \"film_id\"" +
                " where (select \"from_user_id\" from \"likes\"" +
                        " where \"film_id\" IN (select \"film_id\" from \"likes\"" +
                                " where \"from_user_id\"= ?) and \"from_user_id\" not in (?))" +
                " and \"film_id\" not IN (select \"film_id\" from \"likes\"" +
                        " where \"from_user_id\"= ?)))";

        return template.query(sqlQuery, new FilmMapper(), userId, userId, userId);
    }

    @Override
    public void removeFilm(int id) {
        Integer responseId = getIdFromDB(id);

        if (responseId == null) {
            throw new FilmNotFoundException(String.format("Ошибка удаления: фильм с id=%d не найден.", id));
        }

        template.update("DELETE FROM \"films\" WHERE \"film_id\" = ? ", id);
    }

    public Collection<Film> searchFilms(String query, SearchBy searchBy) {
        String director = null;
        String title = null;

        if (searchBy == SearchBy.DIRECTOR) {
            director = "'%" + query.toLowerCase() + "%'";

        } else if (searchBy == SearchBy.TITLE) {
            title = "'%" + query.toLowerCase() + "%'";

        } else {
            director = "'%" + query.toLowerCase() + "%'";
            title = director;
        }

        return template.query("SELECT * FROM \"films\" f " +
                "LEFT JOIN \"mpa_rating\" mr ON f.\"mpa_id\" = mr.\"mpa_id\" " +
                "LEFT JOIN \"film_directors\" fd ON f.\"film_id\" = fd.\"film_id\" " +
                "LEFT JOIN \"directors\" d ON fd.\"director_id\" = d.\"director_id\" " +
                "WHERE LOWER(f.\"film_name\") LIKE " + title +
                " OR LOWER(d.\"director_name\") LIKE " + director, new FilmMapper());
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
        SqlRowSet rowSet = template.queryForRowSet(
                "SELECT \"from_user_id\" FROM \"likes\" WHERE \"film_id\" = ?", id);

        while (rowSet.next()) {
            film.addLike(rowSet.getInt("from_user_id"));
        }
    }

    private void addDirectorsIdsToDTO(Film film, int id) {
        SqlRowSet rowSet = template.queryForRowSet(
                "SELECT * FROM \"directors\"" +
                        "WHERE \"director_id\" IN (" +
                            "(SELECT DISTINCT \"director_id\" " +
                            "FROM \"film_directors\" " +
                            "WHERE \"film_id\" = ?))", id);

        while (rowSet.next()) {
            film.addDirector(new Director(
                    rowSet.getInt("director_id"),
                    rowSet.getString("director_name")
            ));
        }
    }

    class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            int filmId = rs.getInt("film_id");

            Film film = new Film(filmId,
                    rs.getString("film_name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    rs.getInt("rate"),
                    new MPA(rs.getInt("mpa_id"), rs.getString("mpa_name")));

            List<Genre> genres = genreDAO.getFilmGenres(filmId);
            for (Genre genre : genres) {
                film.addGenre(genre);
            }
            addLikesToDTO(film, filmId);
            addDirectorsIdsToDTO(film, filmId);

            return film;
        }
    }
}
