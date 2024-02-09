package it.smartcommunitylabdhub.commons.annotations.validators;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidFieldValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateField {
    String message() default "";

    boolean allowNull() default false;

    String fieldType() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String regex() default "";
}
