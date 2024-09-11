package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.repository.EventFeedRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventFeedService {
    private final EventFeedRepository repository;
    private final UserRepository userRepository;

    public Collection<UserEvent> getEventFeed(long userId) {
        final User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with " + userId));
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
