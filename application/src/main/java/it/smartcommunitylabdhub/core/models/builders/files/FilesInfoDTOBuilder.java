package it.smartcommunitylabdhub.core.models.builders.files;

<<<<<<< HEAD
import java.io.IOException;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

=======
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
>>>>>>> origin/file_info_repo
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.entities.files.FilesInfo;
import it.smartcommunitylabdhub.core.models.entities.FilesInfoEntity;
<<<<<<< HEAD
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FilesInfoDTOBuilder implements Converter<FilesInfoEntity, FilesInfo>{
	private final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;
	
	public FilesInfo build(FilesInfoEntity entity) {
		List<FileInfo> files = null;
		try {
			if((entity.getFiles() != null) && entity.getFiles().length > 0) {
				files = mapper.readValue(entity.getFiles(), new TypeReference<List<FileInfo>>() {});
			}
		} catch (IOException e) {
			log.warn("FilesInfo build error: {}", e.getMessage());
		}
		return FilesInfo.builder().id(entity.getId()).entityName(entity.getEntityName()).entityId(entity.getEntityId()).files(files).build();
	}

	@Override
	public FilesInfo convert(FilesInfoEntity source) {
		return build(source);
	}
=======
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FilesInfoDTOBuilder implements Converter<FilesInfoEntity, FilesInfo> {

    private static final TypeReference<List<FileInfo>> typeRef = new TypeReference<>() {};

    private static final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;

    public FilesInfo build(FilesInfoEntity entity) {
        List<FileInfo> files = null;
        try {
            if ((entity.getFiles() != null) && entity.getFiles().length > 0) {
                files = mapper.readValue(entity.getFiles(), typeRef);
            }
        } catch (IOException e) {
            log.error("FilesInfo build error: {}", e.getMessage());
        }

        return FilesInfo
            .builder()
            .id(entity.getId())
            .entityName(entity.getEntityName())
            .entityId(entity.getEntityId())
            .files(files)
            .build();
    }

    @Override
    public FilesInfo convert(FilesInfoEntity source) {
        return build(source);
    }
>>>>>>> origin/file_info_repo
}
