package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Service
public class ValidationUserService implements ValidationService<User> {

    @Override
    public void validate4Create(User user) {
        validateEmail(user);
        validateLogin(user);
        validateName(user);
        validateBirthday(user);
    }

    @Override
    public void validate4Update(User user) {
        validateId(user);
        validate4Create(user);
    }

    private void validateId(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан.");
        }
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя для отображения не может быть пустым или содержать пробелы.");
        }
    }

    private void validateEmail(User user) {
        String email = user.getEmail();
        if (email == null || email.isBlank() || email.indexOf('@') == -1) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
    }

    private void validateLogin(User user) {
        String login = user.getLogin();
        if (login == null || login.isEmpty() || login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
    }

    private void validateBirthday(User user) {
        if (user.getBirthday() == null) {
            throw new ValidationException("Не указана дата рождения.");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

}
