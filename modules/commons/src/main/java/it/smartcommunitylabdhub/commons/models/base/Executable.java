package it.smartcommunitylabdhub.commons.models.base;

import java.io.Serializable;
import java.util.Map;

public interface Executable extends BaseDTO, MetadataDTO, SpecDTO, StatusDTO {
    Map<String, Serializable> getSpec();

    void setSpec(Map<String, Serializable> spec);
}
