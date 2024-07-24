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
        return storage.get(id)
                .orElseThrow(() -> new NotFoundException("Сущность с id " + id + " не найдена"));
    }

    @Override
    public T create(T model) {
        return storage.create(model);
    }

    @Override
    public T update(T model) {
        return storage.update(model);
    }
}
