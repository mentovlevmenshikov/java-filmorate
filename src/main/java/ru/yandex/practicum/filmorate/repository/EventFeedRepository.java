package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.Collection;

public interface EventFeedRepository {
    Collection<UserEvent> getEventFeed(long userId);

    void addEvent(UserEvent event);
}
