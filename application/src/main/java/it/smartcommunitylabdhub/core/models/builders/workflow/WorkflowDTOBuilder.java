package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WorkflowDTOBuilder implements Converter<WorkflowEntity, Workflow> {

    @Autowired
    CBORConverter cborConverter;

    public Workflow build(WorkflowEntity entity, boolean embeddable) {
        return EntityFactory.create(
            Workflow::new,
            builder ->
                builder
                    .with(dto -> dto.setId(entity.getId()))
                    .with(dto -> dto.setKind(entity.getKind()))
                    .with(dto -> dto.setProject(entity.getProject()))
                    .with(dto -> dto.setName(entity.getName()))
                    .with(dto -> {
                        //read metadata as-is
                        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

                        // Set Metadata for workflow
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

                        //merge into map with override
                        dto.setMetadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()));
                    })
                    .withIfElse(
                        embeddable,
                        (dto, condition) ->
                            Optional
                                .ofNullable(entity.getEmbedded())
                                .filter(embedded -> !condition || embedded)
                                .ifPresent(embedded -> dto.setSpec(cborConverter.reverseConvert(entity.getSpec())))
                    )
                    .withIfElse(
                        embeddable,
                        (dto, condition) ->
                            Optional
                                .ofNullable(entity.getEmbedded())
                                .filter(embedded -> !condition || embedded)
                                .ifPresent(embedded -> dto.setExtra(cborConverter.reverseConvert(entity.getExtra())))
                    )
                    .withIfElse(
                        embeddable,
                        (dto, condition) ->
                            Optional
                                .ofNullable(entity.getEmbedded())
                                .filter(embedded -> !condition || embedded)
                                .ifPresent(embedded ->
                                    dto.setStatus(
                                        MapUtils.mergeMultipleMaps(
                                            cborConverter.reverseConvert(entity.getStatus()),
                                            Map.of("state", entity.getState().toString())
                                        )
                                    )
                                )
                    )
        );
    }

    @Override
    public Workflow convert(WorkflowEntity source) {
        return build(source, false);
    }
}
