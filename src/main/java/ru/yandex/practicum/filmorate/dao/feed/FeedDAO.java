package ru.yandex.practicum.filmorate.dao.feed;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Repository("FeedDAO")
@Primary
@AllArgsConstructor
public class FeedDAO {

    private final JdbcTemplate template;

    public Collection<Feed> getUserFeed(int userId) {
        String sql = "SELECT * FROM \"feed\" WHERE \"user_id\" = ?";
        return template.query(sql, new FeedMapper(), userId);
    }

    public void addLikeEvent(int userId, int entityId) {
        addEventToFeed(userId, EventType.LIKE, OperationType.ADD, entityId);
    }

    public void removeLikeEvent(int userId, int entityId) {
        addEventToFeed(userId, EventType.LIKE, OperationType.REMOVE, entityId);
    }

    public void addReviewEvent(int userId, int entityId) {
        addEventToFeed(userId, EventType.REVIEW, OperationType.ADD, entityId);
    }

    public void updateReviewEvent(int userId, int entityId) {
        addEventToFeed(userId, EventType.REVIEW, OperationType.UPDATE, entityId);
    }

    public void removeReviewEvent(int userId, int entityId) {
        addEventToFeed(userId, EventType.REVIEW, OperationType.REMOVE, entityId);
    }

    public void addFriendEvent(int userId, int entityId) {
        addEventToFeed(userId, EventType.FRIEND, OperationType.ADD, entityId);
    }

    public void removeFriendEvent(int userId, int entityId) {
        addEventToFeed(userId, EventType.FRIEND, OperationType.REMOVE, entityId);
    }

    private void addEventToFeed(int userId, EventType eventType, OperationType operation, int entityId) {
        String sql = "INSERT INTO \"feed\" (\"timestamp\", \"user_id\", \"eventType\", \"operation\", \"entity_id\")" +
                "VALUES (?, ?, ?, ?, ?)";
        template.update(sql,
                Timestamp.from(Instant.now()),
                userId,
                eventType.toString(),
                operation.toString(),
                entityId);
    }

    private static class FeedMapper implements RowMapper<Feed> {
        @Override
        public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Feed(rs.getTimestamp("timestamp").getTime(),
                    rs.getInt("user_id"),
                    rs.getString("eventType"),
                    rs.getString("operation"),
                    rs.getInt("event_id"),
                    rs.getInt("entity_id")
                    );
        }
    }

    private enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    private enum OperationType {
        REMOVE,
        ADD,
        UPDATE
    }
}
