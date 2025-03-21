package it.smartcommunitylabdhub.files.service;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.files.FileInfo;
import it.smartcommunitylabdhub.commons.models.files.FilesInfo;
import it.smartcommunitylabdhub.commons.utils.UUIDKeyGenerator;
import it.smartcommunitylabdhub.core.files.FilesInfoService;
import it.smartcommunitylabdhub.files.persistence.FilesInfoDTOBuilder;
import it.smartcommunitylabdhub.files.persistence.FilesInfoEntity;
import it.smartcommunitylabdhub.files.persistence.FilesInfoEntityBuilder;
import it.smartcommunitylabdhub.files.persistence.FilesInfoRepository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional
@Slf4j
public class FilesInfoServiceImpl implements FilesInfoService {

    @Value("${files.max-column-size}")
    private int maxColumnSize;

    @Autowired
    private FilesInfoDTOBuilder dtoBuilder;

    @Autowired
    private FilesInfoEntityBuilder entityBuilder;

    @Autowired
    private FilesInfoRepository repository;

    private StringKeyGenerator keyGenerator = new UUIDKeyGenerator();

    @Autowired(required = false)
    public void setKeyGenerator(StringKeyGenerator keyGenerator) {
        Assert.notNull(keyGenerator, "key generator can not be null");
        this.keyGenerator = keyGenerator;
    }

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
            dto.setId(keyGenerator.generateKey());
        }

        entity = entityBuilder.convert(dto);

        //check files size before persisting
        if (entity.getFiles() != null && entity.getFiles().length > maxColumnSize) {
            throw new IllegalArgumentException("files column exceeds maximum size " + String.valueOf(maxColumnSize));
        }

        entity = repository.save(entity);
        return dtoBuilder.convert(entity);
    }
}
