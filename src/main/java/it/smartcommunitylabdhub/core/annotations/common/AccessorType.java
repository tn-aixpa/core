package it.smartcommunitylabdhub.core.annotations.common;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Indexed
public @interface AccessorType {
    String kind();

    EntityName entity();
}
