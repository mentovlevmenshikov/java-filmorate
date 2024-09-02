package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

@Service
public class MpaService extends ModelService<MPA> {

    public MpaService(ModelRepository<MPA> modelRepository) {
        super(modelRepository);
    }
}
