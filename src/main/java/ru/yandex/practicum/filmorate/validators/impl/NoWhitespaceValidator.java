package ru.yandex.practicum.filmorate.validators.impl;

import ru.yandex.practicum.filmorate.validators.NoWhitespaceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoWhitespaceValidator implements ConstraintValidator<NoWhitespaceConstraint, String> {
    @Override
    public void initialize(NoWhitespaceConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !value.contains(" ");
    }
}
