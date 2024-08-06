package it.smartcommunitylabdhub.core.services;

<<<<<<< HEAD
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

=======
>>>>>>> origin/file_info_repo
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.entities.files.FilesInfo;
import it.smartcommunitylabdhub.commons.services.entities.FilesInfoService;
import it.smartcommunitylabdhub.core.models.builders.files.FilesInfoDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.files.FilesInfoEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.FilesInfoEntity;
import it.smartcommunitylabdhub.core.repositories.FilesInfoRepository;
import jakarta.validation.constraints.NotNull;
<<<<<<< HEAD
import lombok.extern.slf4j.Slf4j;
=======
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
>>>>>>> origin/file_info_repo

@Service
@Transactional
@Slf4j
public class FilesInfoServiceImpl implements FilesInfoService {
<<<<<<< HEAD
	@Value("${files.max-column-size}")
	private int maxColumnSize;
	
	@Autowired
	private FilesInfoDTOBuilder dtoBuilder;
	
	@Autowired
	private FilesInfoEntityBuilder entityBuilder;
	
	@Autowired
	private FilesInfoRepository repository;

	@Override
	public FilesInfo getFilesInfo(@NotNull String entityName, @NotNull String entityId) throws SystemException {
		log.debug("get files info with id {} / {}", entityId, entityName);
		FilesInfoEntity entity = repository.findByEntityNameAndEntityId(entityName, entityId);
		if(entity != null) { 
			return  dtoBuilder.convert(entity);
		}
		return null;
	}

	@Override
	public FilesInfo saveFilesInfo(@NotNull String entityName, @NotNull String entityId, List<FileInfo> files) throws SystemException, StoreException {
		log.debug("save files info with id {} / {}", entityName, entityId);
		FilesInfo dto = FilesInfo.builder()
				.entityName(entityName)
				.entityId(entityId)
				.files(files)
				.build();
		
		FilesInfoEntity entity = repository.findByEntityNameAndEntityId(entityName, entityId);
        if(entity != null) {
        	dto.setId(entity.getId());
        } else {
        	dto.setId(UUID.randomUUID().toString());
        }
        entity = entityBuilder.convert(dto);
        if((entity.getFiles() != null) && (entity.getFiles().length > maxColumnSize)) {
        	throw new StoreException("files array exceds max value:" + String.valueOf(maxColumnSize));
        }
        repository.save(entity);
		return dto;
	}
	
	
=======

    @Value("${files.max-column-size}")
    private int maxColumnSize;

    @Autowired
    private FilesInfoDTOBuilder dtoBuilder;

    @Autowired
    private FilesInfoEntityBuilder entityBuilder;

    @Autowired
    private FilesInfoRepository repository;

    @Override
    public FilesInfo getFilesInfo(@NotNull String entityName, @NotNull String entityId)
        throws StoreException, SystemException {
        log.debug("get files info for entity {} id {}", entityId, entityName);

        FilesInfoEntity entity = repository.findByEntityNameAndEntityId(entityName, entityId);
        if (entity != null) {
            return dtoBuilder.convert(entity);
        }

        return null;
    }

    @Override
    public FilesInfo saveFilesInfo(@NotNull String entityName, @NotNull String entityId, List<FileInfo> files)
        throws StoreException, SystemException {
        log.debug("save files info for entity {} id {}", entityName, entityId);
        FilesInfo dto = FilesInfo.builder().entityName(entityName).entityId(entityId).files(files).build();

        FilesInfoEntity entity = repository.findByEntityNameAndEntityId(entityName, entityId);
        if (entity != null) {
            dto.setId(entity.getId());
        } else {
            dto.setId(UUID.randomUUID().toString());
        }

        entity = entityBuilder.convert(dto);

        //check files size before persisting
        if (entity.getFiles() != null && entity.getFiles().length > maxColumnSize) {
            throw new IllegalArgumentException("files column exceeds maximum size " + String.valueOf(maxColumnSize));
        }

        entity = repository.save(entity);
        return dtoBuilder.convert(entity);
    }
>>>>>>> origin/file_info_repo
}
