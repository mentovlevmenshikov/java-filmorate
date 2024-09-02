package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

@Service
public class GenreService extends ModelService<Genre> {

    public GenreService(ModelRepository<Genre> repository) {
        super(repository);
    }
}
