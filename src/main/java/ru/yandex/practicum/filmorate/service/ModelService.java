package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

import java.util.Collection;

@RequiredArgsConstructor
public class ModelService<T> implements StorageService<T> {
    protected final ModelRepository<T> repository;

    @Override
    public Collection<T> getAll() {
        return repository.getAll();
    }

    @Override
    public T get(long id) {
        return repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Сущность с id " + id + " не найдена"));
    }

    @Override
    public T create(T model) {
        return repository.create(model);
    }

    @Override
    public T update(T model) {
        return repository.update(model);
    }
}
