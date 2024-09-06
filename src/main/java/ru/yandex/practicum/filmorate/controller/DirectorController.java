package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAll() {
        log.info("Запрос всех режиссеров");
        Collection<Director> allDirectors = directorService.getAll();
        log.info("Возврат режиссеров в кол-ве: {}", allDirectors.size());
        return allDirectors;
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable long id) {
        log.info("Запрос режиссера с id: {}", id);
        Director director = directorService.get(id);
        log.info("Возврат режиссера: {}", director);
        return director;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@RequestBody @Valid Director director) {
        log.info("Создание режиссера: {}", director);
        Director created = directorService.create(director);
        log.info("Создан режиссер: {}", director);
        return created;
    }

    @PutMapping
    public Director update(@RequestBody @Validated(Update.class) Director director) {
        log.info("Обновление режиссера: {}", director);
        Director updated = directorService.update(director);
        log.info("Обновленный режиссер: {}", director);
        return updated;
    }
}
