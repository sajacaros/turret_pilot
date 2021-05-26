package com.bnpinnovation.turret.controller.advice;

import com.bnpinnovation.turret.dto.ErrorMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalDefaultExceptionHandler {
    @ExceptionHandler(value = EntityExistsException.class)
    public ErrorMessage entityExistsException(EntityExistsException e) {
        return new ErrorMessage(3, e.getMessage());
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ErrorMessage entityNotFoundException(EntityNotFoundException e) {
        return new ErrorMessage(4, e.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ErrorMessage entityNotFoundException(IllegalArgumentException e) {
        return new ErrorMessage(5, e.getMessage());
    }
}
