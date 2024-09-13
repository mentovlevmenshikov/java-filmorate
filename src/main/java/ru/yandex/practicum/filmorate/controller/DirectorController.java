package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.validator.Update;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.ModelService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController extends Controller<Director> {
    private final DirectorService directorService;

    public DirectorController(ModelService<Director> modelService, DirectorService directorService) {
        super(null, modelService);
        this.directorService = directorService;
    }

    @Override
    @GetMapping
    public Collection<Director> getAll() {
        log.info("Запрос всех режиссеров");
        Collection<Director> allDirectors = directorService.getAll();
        log.info("Возврат режиссеров в кол-ве: {}", allDirectors.size());
        return allDirectors;
    }

    @Override
    @GetMapping("/{id}")
    public Director get(@PathVariable long id) {
        log.info("Запрос режиссера с id: {}", id);
        Director director = directorService.get(id);
        log.info("Возврат режиссера: {}", director);
        return director;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@RequestBody @Valid Director director) {
        log.info("Создание режиссера: {}", director);
        Director created = directorService.create(director);
        log.info("Создан режиссер: {}", director);
        return created;
    }

    @Override
    @PutMapping
    public Director update(@RequestBody @Validated(Update.class) Director director) {
        log.info("Обновление режиссера: {}", director);
        Director updated = directorService.update(director);
        log.info("Обновленный режиссер: {}", director);
        return updated;
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Удаление режиссера с id: {}", id);
        directorService.delete(id);
        log.info("Режиссер удален");
    }
}
