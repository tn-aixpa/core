package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.project.specs.ProjectSpec;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProjectDTOBuilder implements Converter<ProjectEntity, Project> {

    private final CBORConverter cborConverter;

    public ProjectDTOBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    public Project build(ProjectEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

        // build metadata
        ProjectMetadata metadata = new ProjectMetadata();
        metadata.configure(meta);

        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }
        metadata.setProject(entity.getProject());
        metadata.setSource(entity.getSource());
        metadata.setCreated(
            entity.getCreated() != null
                ? LocalDateTime.ofInstant(entity.getCreated().toInstant(), ZoneOffset.UTC)
                : null
        );
        metadata.setUpdated(
            entity.getUpdated() != null
                ? LocalDateTime.ofInstant(entity.getUpdated().toInstant(), ZoneOffset.UTC)
                : null
        );

        //transform base spec to full spec
        ProjectBaseSpec baseSpec = new ProjectBaseSpec();
        baseSpec.configure(cborConverter.reverseConvert(entity.getSpec()));
        ProjectSpec spec = new ProjectSpec();
        spec.configure(baseSpec.toMap());

        return Project
            .builder()
            .id(entity.getId())
            .name(entity.getName())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .spec(cborConverter.reverseConvert(entity.getSpec()))
            .extra(cborConverter.reverseConvert(entity.getExtra()))
            .status(
                MapUtils.mergeMultipleMaps(
                    cborConverter.reverseConvert(entity.getStatus()),
                    Map.of("state", entity.getState().toString())
                )
            )
            .build();
    }

    @Override
    public Project convert(ProjectEntity source) {
        return build(source);
    }
}
