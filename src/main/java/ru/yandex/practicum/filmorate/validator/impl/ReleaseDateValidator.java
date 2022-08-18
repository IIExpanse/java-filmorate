package ru.yandex.practicum.filmorate.validator.impl;

import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {

    public static LocalDate earliestPossibleDate;

    @Override
    public void initialize(ReleaseDateConstraint constraintAnnotation) {
        earliestPossibleDate = LocalDate.of(
                constraintAnnotation.year()[0],
                constraintAnnotation.month()[0],
                constraintAnnotation.day()[0]);
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(earliestPossibleDate.minusDays(1));
    }
}
