package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

public interface StorageService<T> {
    Collection<T> getAll();

    T get(long id);

    T create(T model);

    T update(T model);
}
