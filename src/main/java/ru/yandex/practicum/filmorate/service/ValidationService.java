package ru.yandex.practicum.filmorate.service;

public interface ValidationService<T> {
    void validate4Create(T t);
    void validate4Update(T t);
}
