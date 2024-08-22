package ru.yandex.practicum.filmorate.repository.inmemory;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryModelRepository<T extends Model> implements ModelRepository<T> {
    private long counterId = 0;
    protected final Map<Long, T> models = new HashMap<>();

    @Override
   public Optional<T> getById(long id) {
       return Optional.ofNullable(models.get(id));
   }

   @Override
    public Collection<T> getAll() {
       return models.values();
    }

    @Override
    public T create(T model) {
        model.setId(getNextId());
        models.put(model.getId(), model);
        return find(model);
    }

    @Override
    public T update(T model) {
       T findedModel = find(model);
        models.put(findedModel.getId(), model);
        return find(model);
    }

    private T find(T model) {
        T findedModel = models.get(model.getId());
        if (findedModel == null) {
            throw new NotFoundException(String.format("Сущность с id = %s не найдена.", model.getId()));
        }

        return findedModel;
    }

    protected long getNextId() {
        return ++counterId;
    }
}
