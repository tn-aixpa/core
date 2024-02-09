package it.smartcommunitylabdhub.commons.models.specs;

import java.util.Map;

/**
 * Base spec interface. Should be implemented by all specific Spec Object.
 */
public interface Spec {
    Map<String, Object> toMap();

    void configure(Map<String, Object> data);
}
