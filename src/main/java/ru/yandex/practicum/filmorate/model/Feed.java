package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Feed {
    private final Long timestamp;
    private final int userId;
    private final String eventType;
    private final String operation;
    private final int eventId;
    private final int entityId;
}
