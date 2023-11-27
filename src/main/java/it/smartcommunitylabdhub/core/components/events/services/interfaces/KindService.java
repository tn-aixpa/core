package it.smartcommunitylabdhub.core.components.events.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.run.Run;

public interface KindService<T> {
    T run(Run runDTO);
}
