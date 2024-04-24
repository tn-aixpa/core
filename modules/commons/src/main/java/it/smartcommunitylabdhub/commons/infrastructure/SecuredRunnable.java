package it.smartcommunitylabdhub.commons.infrastructure;

import java.io.Serializable;

public interface SecuredRunnable {
    Serializable getCredentials();
    void setCredentials(Serializable credentials);
}
