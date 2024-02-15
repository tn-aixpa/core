package it.smartcommunitylabdhub.commons.models.specs;

import java.io.Serializable;
import java.util.Map;

/**
 * A SPEC is a static view on a map: it fully describes the content by mapping variables
 * into named (and typed) fields.
 *
 * Base spec interface. Should be implemented by all specific Spec Object.
 */
public interface Spec {
    Map<String, Serializable> toMap();

    void configure(Map<String, Serializable> data);
}
