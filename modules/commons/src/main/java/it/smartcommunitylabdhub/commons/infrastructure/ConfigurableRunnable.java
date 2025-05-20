package it.smartcommunitylabdhub.commons.infrastructure;

import java.util.Collection;

public interface ConfigurableRunnable {
    void setConfigurations(Collection<Configuration> configurations);
}
