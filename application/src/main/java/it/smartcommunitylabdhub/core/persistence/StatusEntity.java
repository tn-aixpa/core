package it.smartcommunitylabdhub.core.persistence;

import java.io.Serializable;

public interface StatusEntity extends Serializable {
    byte[] getStatus();

    void setStatus(byte[] status);
}
