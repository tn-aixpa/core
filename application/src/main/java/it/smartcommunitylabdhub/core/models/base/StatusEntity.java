package it.smartcommunitylabdhub.core.models.base;

import java.io.Serializable;

public interface StatusEntity extends Serializable {
    byte[] getStatus();

    void setStatus(byte[] status);
}
