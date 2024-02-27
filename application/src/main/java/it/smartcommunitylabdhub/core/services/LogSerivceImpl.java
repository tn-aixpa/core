package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.builders.log.LogDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.log.LogEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.repositories.LogRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LogSerivceImpl implements LogService {

    @Autowired
    LogRepository logRepository;

    @Autowired
    LogEntityBuilder logEntityBuilder;

    @Autowired
    LogDTOBuilder logDTOBuilder;

    @Override
    public Page<Log> getLogs(Pageable pageable) {
        try {
            Page<LogEntity> logPage = this.logRepository.findAll(pageable);

            return new PageImpl<>(
                logPage.getContent().stream().map(log -> logDTOBuilder.build(log)).collect(Collectors.toList()),
                pageable,
                logPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Log getLog(String uuid) {
        return logRepository
            .findById(uuid)
            .map(log -> {
                try {
                    return logDTOBuilder.build(log);
                } catch (CustomException e) {
                    throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            })
            .orElseThrow(() ->
                new CoreException("LogNotFound", "The log you are searching for does not exist.", HttpStatus.NOT_FOUND)
            );
    }

    @Override
    public boolean deleteLog(String uuid) {
        try {
            this.logRepository.deleteById(uuid);
            return true;
        } catch (Exception e) {
            throw new CoreException("InternalServerError", "cannot delete artifact", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Log createLog(Log logDTO) {
        if (logDTO.getId() != null && logRepository.existsById(logDTO.getId())) {
            throw new CoreException("DuplicateLogId", "Cannot create the log", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<LogEntity> savedLog = Optional
            .of(logDTO)
            .map(logEntityBuilder::build)
            .map(this.logRepository::saveAndFlush);

        return savedLog
            .map(log -> logDTOBuilder.build(log))
            .orElseThrow(() ->
                new CoreException("InternalServerError", "Error saving log", HttpStatus.INTERNAL_SERVER_ERROR)
            );
    }

    @Override
    public Page<Log> getLogsByRunUuid(String uuid, Pageable pageable) {
        Page<LogEntity> logPage = logRepository.findByRun(uuid, pageable);
        return new PageImpl<>(
            logPage
                .getContent()
                .stream()
                .map(log -> {
                    try {
                        return logDTOBuilder.build(log);
                    } catch (CustomException e) {
                        throw new CoreException(
                            "InternalServerError",
                            e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                        );
                    }
                })
                .collect(Collectors.toList()),
            pageable,
            logPage.getTotalElements()
        );
    }
}
