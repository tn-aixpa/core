package it.smartcommunitylabdhub.core.models.base;

import java.io.Serializable;

public interface SpecEntity extends Serializable {
    byte[] getSpec();

    void setSpec(byte[] spec);
}
