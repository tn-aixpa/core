package it.smartcommunitylabdhub.commons.infrastructure;

import java.util.Collection;

public interface SecuredRunnable {
    Collection<Credentials> getCredentials();
    void setCredentials(Collection<Credentials> credentials);
}
