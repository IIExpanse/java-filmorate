package ru.yandex.practicum.filmorate.validators.impl;

import ru.yandex.practicum.filmorate.validators.ReleaseDateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {

    public static LocalDate earliestPossibleDate;

    @Override
    public void initialize(ReleaseDateConstraint constraintAnnotation) {
        earliestPossibleDate = LocalDate.parse(constraintAnnotation.earliestPossibleDate()[0]);
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(earliestPossibleDate.minusDays(1));
    }
}
