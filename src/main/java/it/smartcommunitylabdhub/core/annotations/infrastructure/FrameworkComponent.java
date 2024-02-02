package it.smartcommunitylabdhub.core.annotations.infrastructure;

import java.lang.annotation.*;

/**
 * Indicates that the annotated class is a framework component responsible for
 * providing functionality related to a specific framework.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
//@Component
public @interface FrameworkComponent {

    /**
     * Specifies the framework type for which the component is designed.
     * Possible values include: LOCAL, SERVE, DEPLOY, etc.
     */
    String framework(); // Framework type (e.g. local, serve, deploy)

}
