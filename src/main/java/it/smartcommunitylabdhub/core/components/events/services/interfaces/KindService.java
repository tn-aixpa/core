package it.smartcommunitylabdhub.core.components.events.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.run.XRun;

public interface KindService<T> {
    T run(XRun runDTO);
}
