package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

@Service
public class DirectorService extends ModelService<Director> {
    public DirectorService(ModelRepository<Director> modelRepository) {
        super(modelRepository);
    }
}
