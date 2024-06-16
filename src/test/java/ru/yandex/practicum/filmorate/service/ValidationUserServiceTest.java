package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUserServiceTest {

    private final ValidationService<User> validationService = new ValidationUserService();

    private static User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setLogin("login");
        user.setEmail("email@ya.ru");
        user.setName("name user");
        user.setBirthday(LocalDate.now().minusYears(18));
    }

    @Test
    void validateEmail() {
        user.setEmail("  ");
        assertThrows(ValidationException.class, () -> validationService.validate4Update(user), "Email не может пустым");

        user.setEmail(null);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(user), "Email не может пустым");

        user.setEmail("myemail.ru");
        assertThrows(ValidationException.class, () -> validationService.validate4Update(user), "Email должен содержать @");

        user.setEmail("my@emaili.ru");
        assertDoesNotThrow(() -> validationService.validate4Update(user), "Email должен быть корректным");
    }

    @Test
    void validateLogin() {
        user.setLogin(" ");
        assertThrows(ValidationException.class, () -> validationService.validate4Update(user), "Логин не может пустым");

        user.setLogin(null);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(user), "Логин не может пустым");

        user.setLogin("login");
        assertDoesNotThrow(() -> validationService.validate4Update(user), "Логин должен быть верным");
    }

    @Test
    void validateBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> validationService.validate4Update(user), "Дата рождения не может быть в будущем");

        user.setBirthday(null);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(user), "Дата рождения не может быть null");

        user.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> validationService.validate4Update(user), "Дата рождения может быть сегоднешним днем");
    }

    @Test
    void validateId() {
        user.setId(null);
        assertThrows(ValidationException.class, () -> validationService.validate4Update(user), "Id пользователя не может быть null для обновления");

        assertDoesNotThrow(() -> validationService.validate4Create(user), "Id пользователя может быть null для создания");
    }
}