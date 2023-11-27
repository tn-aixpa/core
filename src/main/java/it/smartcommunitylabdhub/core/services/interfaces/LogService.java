package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.log.Log;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LogService {

    List<Log> getLogs(Pageable pageable);

    Log getLog(String uuid);

    List<Log> getLogsByRunUuid(String uuid);

    boolean deleteLog(String uuid);

    Log createLog(Log logDTO);

}
