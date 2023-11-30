package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.metadata.WorkflowMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WorkflowDTOBuilder {

    @Autowired
    MetadataConverter<WorkflowMetadata> metadataConverter;

    public Workflow build(
            WorkflowEntity workflow,
            boolean embeddable) {
        return EntityFactory.create(Workflow::new, workflow, builder -> builder
                .with(dto -> dto.setId(workflow.getId()))
                .with(dto -> dto.setKind(workflow.getKind()))
                .with(dto -> dto.setProject(workflow.getProject()))
                .with(dto -> dto.setName(workflow.getName()))
                .with(dto -> dto.setMetadata(Optional
                        .ofNullable(metadataConverter
                                .reverseByClass(workflow
                                                .getMetadata(),
                                        WorkflowMetadata.class))
                        .orElseGet(WorkflowMetadata::new)))

                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(workflow.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setSpec(ConversionUtils.reverse(
                                        workflow.getSpec(),
                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(workflow.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setExtra(ConversionUtils.reverse(
                                        workflow.getExtra(),
                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(workflow.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setCreated(workflow.getCreated())))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(workflow.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setUpdated(workflow.getUpdated())))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(workflow.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setEmbedded(workflow
                                        .getEmbedded())))
                .withIfElse(embeddable, (dto, condition) ->
                        Optional.ofNullable(workflow.getEmbedded())
                                .filter(embedded -> !condition
                                        || (condition && embedded))
                                .ifPresent(embedded -> dto
                                        .setStatus(ConversionUtils.reverse(
                                                workflow.getStatus(), "cbor")
                                        )
                                )

                )
                .with(dto -> dto.setCreated(workflow.getCreated()))
                .with(dto -> dto.setUpdated(workflow.getUpdated()))


        );
    }
}
