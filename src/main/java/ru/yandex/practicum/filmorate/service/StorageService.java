package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

public interface StorageService<T> {
    public Collection<T> getAll();

    public T get(long id);

    public T create(T model);

    public T update(T model);
}
