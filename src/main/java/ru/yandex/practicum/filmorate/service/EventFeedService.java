package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.repository.EventFeedRepository;

import java.time.Instant;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventFeedService {
    private final EventFeedRepository repository;

    public Collection<UserEvent> getEventFeed(long userId) {
        return repository.getEventFeed(userId);
    }

    public void addEvent(long userId, EventType type, EventOperation operation, long entityId) {
        UserEvent event = UserEvent.builder()
                .userId(userId)
                .eventType(type)
                .operation(operation)
                .entityId(entityId)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        repository.addEvent(event);
    }
}
