package it.smartcommunitylabdhub.core.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.entities.files.FilesInfo;
import it.smartcommunitylabdhub.commons.services.entities.FilesInfoService;
import it.smartcommunitylabdhub.core.models.builders.files.FilesInfoDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.files.FilesInfoEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.FilesInfoEntity;
import it.smartcommunitylabdhub.core.repositories.FilesInfoRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class FilesInfoServiceImpl implements FilesInfoService {
	
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
	public FilesInfo saveFilesInfo(@NotNull String entityName, @NotNull String entityId, List<FileInfo> files) throws SystemException {
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
        repository.save(entity);
		return dto;
	}
	
	
}
