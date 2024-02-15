package it.smartcommunitylabdhub.commons.annotations.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidFieldValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateField {
    String message() default "";

    boolean allowNull() default false;

    String fieldType() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String regex() default "";
}
