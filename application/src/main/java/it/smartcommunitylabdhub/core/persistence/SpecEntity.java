package it.smartcommunitylabdhub.core.persistence;

import java.io.Serializable;

public interface SpecEntity extends Serializable {
    byte[] getSpec();

    void setSpec(byte[] spec);
}
