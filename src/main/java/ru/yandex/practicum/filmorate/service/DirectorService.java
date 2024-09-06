package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DeleteStorage;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

@Service
public class DirectorService extends ModelService<Director> {
    private final DeleteStorage deleteStorage;

    public DirectorService(ModelRepository<Director> modelRepository) {
        super(modelRepository);
        deleteStorage = (DeleteStorage) modelRepository;
    }

    public void delete(long id) {
        deleteStorage.delete(id);
    }
}
