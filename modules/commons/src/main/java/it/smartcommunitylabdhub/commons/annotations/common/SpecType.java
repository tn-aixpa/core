package it.smartcommunitylabdhub.commons.annotations.common;

import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Indexed;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Indexed
public @interface SpecType {
    String runtime() default "";

    String kind();

    EntityName entity();

    Class<?> factory();
}
