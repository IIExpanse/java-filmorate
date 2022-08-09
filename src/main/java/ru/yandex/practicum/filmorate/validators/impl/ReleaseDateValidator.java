package ru.yandex.practicum.filmorate.validators.impl;

import ru.yandex.practicum.filmorate.validators.ReleaseDateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {

    public static final LocalDate CINEMA_BIRTH_DAY = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(ReleaseDateConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(CINEMA_BIRTH_DAY.minusDays(1));
    }
}
