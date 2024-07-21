package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.ModelStorage;

import java.util.Collection;

@RequiredArgsConstructor
public class ModelService<T> implements StorageService<T> {
    protected final ModelStorage<T> storage;

    @Override
    public Collection<T> getAll() {
        return storage.getAll();
    }

    @Override
    public T get(long id) {
        final T t = storage.get(id)
                .orElseThrow(() -> new NotFoundException("Сущность с id " + id + " не найдена"));
        return t;
    }

    @Override
    public T create(T model) {
        T created = storage.create(model);
        return created;
    }

    @Override
    public T update(T model) {
        T updated = storage.update(model);
        return updated;
    }
}
