package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Controller<T> {
    private long counterId = 0;
    protected final Map<Long, T> models = new HashMap<>();

    public abstract Collection<T> getAll();

    public abstract T create(T model);

    public abstract T update(T model);

    protected long getNextId() {
        return ++counterId;
    }
}
