package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WorkflowDTOBuilder {

    @Autowired
    MetadataConverter<WorkflowMetadata> metadataConverter;

    public Workflow build(WorkflowEntity workflow, boolean embeddable) {
        return EntityFactory.create(
            Workflow::new,
            workflow,
            builder ->
                builder
                    .with(dto -> dto.setId(workflow.getId()))
                    .with(dto -> dto.setKind(workflow.getKind()))
                    .with(dto -> dto.setProject(workflow.getProject()))
                    .with(dto -> dto.setName(workflow.getName()))
                    .with(dto -> {
                        // Set Metadata for workflow
                        WorkflowMetadata workflowMetadata = Optional
                            .ofNullable(
                                metadataConverter.reverseByClass(workflow.getMetadata(), WorkflowMetadata.class)
                            )
                            .orElseGet(WorkflowMetadata::new);

                        if (!StringUtils.hasText(workflowMetadata.getVersion())) {
                            workflowMetadata.setVersion(workflow.getId());
                        }
                        if (!StringUtils.hasText(workflowMetadata.getName())) {
                            workflowMetadata.setName(workflow.getName());
                        }
                        workflowMetadata.setProject(workflow.getProject());
                        workflowMetadata.setEmbedded(workflow.getEmbedded());
                        workflowMetadata.setCreated(workflow.getCreated());
                        workflowMetadata.setUpdated(workflow.getUpdated());
                        dto.setMetadata(workflowMetadata);
                    })
                    .withIfElse(
                        embeddable,
                        (dto, condition) ->
                            Optional
                                .ofNullable(workflow.getEmbedded())
                                .filter(embedded -> !condition || embedded)
                                .ifPresent(embedded -> dto.setSpec(ConversionUtils.reverse(workflow.getSpec(), "cbor")))
                    )
                    .withIfElse(
                        embeddable,
                        (dto, condition) ->
                            Optional
                                .ofNullable(workflow.getEmbedded())
                                .filter(embedded -> !condition || embedded)
                                .ifPresent(embedded ->
                                    dto.setExtra(ConversionUtils.reverse(workflow.getExtra(), "cbor"))
                                )
                    )
                    .withIfElse(
                        embeddable,
                        (dto, condition) ->
                            Optional
                                .ofNullable(workflow.getEmbedded())
                                .filter(embedded -> !condition || embedded)
                                .ifPresent(embedded ->
                                    dto.setStatus(
                                        MapUtils.mergeMultipleMaps(
                                            ConversionUtils.reverse(workflow.getStatus(), "cbor"),
                                            Map.of("state", workflow.getState())
                                        )
                                    )
                                )
                    )
        );
    }
}
