package it.smartcommunitylabdhub.commons.annotations.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface ValidatorComponent {
    String runtime(); // runtime can be dbt, nefertem, dss, kfp...

    String task() default ""; // define the task type that have to be executed by the framework for
// a specific runtime
}
