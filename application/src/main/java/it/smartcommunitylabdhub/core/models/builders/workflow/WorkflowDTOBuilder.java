package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WorkflowDTOBuilder implements Converter<WorkflowEntity, Workflow> {

    private final CBORConverter cborConverter;

    public WorkflowDTOBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    public Workflow build(WorkflowEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

        // build metadata
        WorkflowMetadata metadata = new WorkflowMetadata();
        metadata.configure(meta);

        if (!StringUtils.hasText(metadata.getVersion())) {
            metadata.setVersion(entity.getId());
        }
        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }
        metadata.setProject(entity.getProject());
        metadata.setEmbedded(entity.getEmbedded());
        metadata.setCreated(entity.getCreated());
        metadata.setUpdated(entity.getUpdated());

        return Workflow
            .builder()
            .id(entity.getId())
            .name(entity.getName())
            .kind(entity.getKind())
            .project(entity.getProject())
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
    public Workflow convert(WorkflowEntity source) {
        return build(source);
    }
}
