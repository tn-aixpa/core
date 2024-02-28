package it.smartcommunitylabdhub.commons.annotations.infrastructure;

import java.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Indicates that the annotated class is a monitor component responsible for
 * providing functionality related to a specific monitor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface MonitorComponent {
    /**
     * Specifies the monitor type for which the component is designed.
     * Possible values include: LOCAL, SERVE, DEPLOY, etc.
     */
    String framework(); // Framework type (e.g. local, serve, deploy)
}
