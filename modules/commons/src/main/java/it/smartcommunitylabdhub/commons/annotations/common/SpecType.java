package it.smartcommunitylabdhub.commons.annotations.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Indexed;

import it.smartcommunitylabdhub.commons.models.entities.EntityName;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Indexed
public @interface SpecType {
    String runtime() default "";

    String kind();

    EntityName entity();
// Class<? extends SpecFactory<? extends Spec>> factory();
}
