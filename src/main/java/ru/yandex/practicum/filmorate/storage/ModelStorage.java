package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;

public interface ModelStorage<T> {

    Optional<T> get(long id);

    Collection<T> getAll();

    T create(T model);

    T update(T model);
}
