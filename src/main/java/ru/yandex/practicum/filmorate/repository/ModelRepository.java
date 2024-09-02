package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;

public interface ModelRepository<T> {

    Optional<T> getById(long id);

    Collection<T> getAll();

    T create(T model);

    T update(T model);
}
