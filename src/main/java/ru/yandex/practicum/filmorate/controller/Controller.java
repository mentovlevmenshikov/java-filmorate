package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.service.ModelService;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.Collection;

@RequiredArgsConstructor
public abstract class Controller<T> {

    protected final ValidationService<T> validationService;
    protected final ModelService<T> modelService;

    public abstract Collection<T> getAll();

    public abstract T get(long id);

    public abstract T create(T model);

    public abstract T update(T model);
}
