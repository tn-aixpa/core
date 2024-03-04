package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Service for managing artifacts
 */
public interface LogService {
    Page<Log> getLogs(Pageable pageable);

    Log getLog(String uuid);

    Page<Log> getLogsByRunUuid(String uuid, Pageable pageable);

    boolean deleteLog(String uuid);

    Log createLog(Log logDTO);
}
