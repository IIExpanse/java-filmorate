package ru.yandex.practicum.filmorate.dao.user;

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
import ru.yandex.practicum.filmorate.dao.feed.FeedDAO;
import ru.yandex.practicum.filmorate.exception.friend.FriendAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.friend.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.*;
import java.util.Collection;

@Repository("UserDAO")
@Primary
@AllArgsConstructor
public class UserDAO implements UserStorage {

    private final JdbcTemplate template;
    private final FeedDAO feed;

    @Override
    public User getUser(int id) {
        User user;
        try {
            user = template.queryForObject("SELECT * FROM \"users\" WHERE \"user_id\" = " + id, new UserMapper());
        } catch (DataAccessException e) {
            throw new UserNotFoundException(
                    String.format("Ошибка получения: пользователь с id=%d не найден.", id));
        }

        SqlRowSet rowSet = template.queryForRowSet(
                "SELECT \"to_user_id\" FROM \"friendships_sent\" WHERE \"from_user_id\" = ?", id);
        while (rowSet.next()) {
            if (user != null) {
                user.addFriend(rowSet.getInt("to_user_id"));

            } else throw new RuntimeException("Ошибка при получении пользователя.");
        }

        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return template.query("SELECT * FROM \"users\"", new UserMapper());
    }

    @Override
    public int addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Number id;

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"users\" (\"email\", \"login\", \"user_name\", \"birthday\")" +
                            "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        id = keyHolder.getKey();
        if (id != null) {
            return id.intValue();

        } else throw new RuntimeException("Ошибка: пользователь не был добавлен.");
    }

    @Override
    public void addFriend(int targetUserId, int friendId) {
        SqlRowSet rowSet = template.queryForRowSet(
                "SELECT * FROM \"friendships_sent\" WHERE \"from_user_id\" = ? AND \"to_user_id\" = ?",
                targetUserId, friendId);

        if (rowSet.next()) {
            feed.addFriendEvent(targetUserId, friendId);
            throw new FriendAlreadyAddedException(
                    String.format("Ошибка при добавлении друга для пользователя с id=%d: " +
                            "друг с id=%d уже добавлен.", targetUserId, friendId)
            );
        }

        try {
            template.update("INSERT INTO \"friendships_sent\" (\"from_user_id\", \"to_user_id\")" +
                    "VALUES (?, ?)", targetUserId, friendId);
            feed.addFriendEvent(targetUserId, friendId);
        } catch (DataIntegrityViolationException e) {
            throw new UserNotFoundException(String.format("Ошибка при добавлении друга с id=%d" +
                    " для пользователя с id=%d: один или оба пользователя не найдены.", friendId, targetUserId));
        }
    }

    @Override
    public void updateUser(User user, int id) {
        Integer responseId = getUserIdFromDB(id);

        if (responseId == null) {
            throw new UserNotFoundException(
                    String.format("Ошибка обновления: пользователь с id=%d не найден.", id));
        }

        template.update("UPDATE \"users\" SET \"email\" = ?, \"login\" = ?, \"user_name\" = ?, \"birthday\" = ?" +
                        "WHERE \"user_id\" = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                id);
    }

    @Override
    public void removeFriend(int targetUserId, int friendId) {
        Integer responseId = getUserIdFromDB(targetUserId);
        if (responseId == null) {
            throw new UserNotFoundException(
                    String.format("Ошибка при удалении друга с id=%d у пользователя с id=%d: " +
                            "пользователь не найден.", friendId, targetUserId)
            );
        }

        SqlRowSet rowSet = template.queryForRowSet(
                "SELECT * FROM \"friendships_sent\" WHERE \"from_user_id\" = ? AND \"to_user_id\" = ?",
                targetUserId, friendId);
        if (!rowSet.next()) {
            throw new FriendNotFoundException(
                    String.format("Ошибка при удалении друга у пользователя с id=%d: " +
                            "друг с id=%d не найден.", targetUserId, friendId)
            );
        }
        template.update("DELETE FROM \"friendships_sent\" WHERE \"from_user_id\" = ? AND \"to_user_id\" = ?",
                targetUserId, friendId);
        feed.removeFriendEvent(targetUserId, friendId);
    }

    @Override
    public void removeUser(int id) {
        Integer responseId = getUserIdFromDB(id);

        if (responseId == null) {
            throw new UserNotFoundException(
                    String.format("Ошибка удаления: пользователь с id=%d не найден.", id));
        }

        template.update("DELETE FROM \"users\" WHERE \"user_id\" = ?", id);
    }

    private Integer getUserIdFromDB(int id) {
        Integer result;
        try {
            result = template.queryForObject("SELECT \"user_id\" FROM \"users\" WHERE \"user_id\" = " + id,
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getInt("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("user_name"),
                    rs.getDate("birthday").toLocalDate()
            );
        }
    }
}
