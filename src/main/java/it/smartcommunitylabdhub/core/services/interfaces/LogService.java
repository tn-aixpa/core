package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.log.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogService {

    Page<Log> getLogs(Pageable pageable);

    Log getLog(String uuid);

    Page<Log> getLogsByRunUuid(String uuid, Pageable pageable);

    boolean deleteLog(String uuid);

    Log createLog(Log logDTO);

}
