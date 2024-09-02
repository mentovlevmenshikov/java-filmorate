package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.validator.Update;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<MPA> getAll() {
        log.info("Запрос всех MPA");
        Collection<MPA> allMpa = mpaService.getAll();
        log.info("Возврат MPA в кол-ве: {}", allMpa.size());
        return allMpa;
    }

    @GetMapping("/{id}")
    public MPA getById(@PathVariable long id) {
        log.info("Запрос MPA с id: {}", id);
        MPA mpa = mpaService.get(id);
        log.info("Возврат MPA: {}", mpa);
        return mpa;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MPA create(@RequestBody @Valid MPA mpa) {
        log.info("Создание MPA: {}", mpa);
        MPA created = mpaService.create(mpa);
        log.info("Создан MPA: {}", mpa);
        return created;
    }

    @PutMapping
    public MPA update(@RequestBody @Validated(Update.class) MPA mpa) {
        log.info("Обновление MPA: {}", mpa);
        MPA updated = mpaService.update(mpa);
        log.info("Обновленный MPA: {}", mpa);
        return updated;
    }
}
