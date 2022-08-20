package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.validator.impl.NoWhitespaceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoWhitespaceValidator.class)
public @interface NoWhitespaceConstraint {

    String message() default "Логин не может содержать пробелы";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
