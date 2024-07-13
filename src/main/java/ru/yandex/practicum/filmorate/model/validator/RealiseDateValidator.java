package ru.yandex.practicum.filmorate.model.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class RealiseDateValidator implements ConstraintValidator<RealiseDateConstraint, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        LocalDate minReleasDate = LocalDate.of(1895, 11, 28);
        String message = String.format("Дата релиза должна быть не раньше '%s'", minReleasDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return value.isAfter(minReleasDate);
    }
}
