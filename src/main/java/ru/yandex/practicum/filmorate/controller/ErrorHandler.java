package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @Value("${errorResponse.show.stackTrace}")
    private boolean showStackTraceInResponse;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.error("Error unknown:", e);
        return new ErrorResponse(e, showStackTraceInResponse);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Not found: {}", e.getMessage());
        return new ErrorResponse(e, showStackTraceInResponse);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Validation error: {}", e.getMessage());
        return new ErrorResponse(e, showStackTraceInResponse);
    }
}