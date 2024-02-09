package it.smartcommunitylabdhub.core.annotations.common;

import java.lang.annotation.*;
import org.springframework.web.bind.annotation.Mapping;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface ApiVersion {
    String value() default "v1";
}
