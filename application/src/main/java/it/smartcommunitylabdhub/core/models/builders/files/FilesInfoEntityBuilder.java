package it.smartcommunitylabdhub.core.models.builders.files;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.files.FilesInfo;
import it.smartcommunitylabdhub.core.models.entities.FilesInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FilesInfoEntityBuilder implements Converter<FilesInfo, FilesInfoEntity> {

    private final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;

    public FilesInfoEntity build(FilesInfo dto) {
        byte[] value = null;
        try {
            if (dto.getFiles() != null) {
                value = mapper.writeValueAsBytes(dto.getFiles());
            }
        } catch (JsonProcessingException e) {
            log.error("FilesInfoEntity build error: {}", e.getMessage());
        }

        return FilesInfoEntity
            .builder()
            .id(dto.getId())
            .entityName(dto.getEntityName())
            .entityId(dto.getEntityId())
            .files(value)
            .build();
    }

    @Override
    public FilesInfoEntity convert(FilesInfo source) {
        return build(source);
    }
}
