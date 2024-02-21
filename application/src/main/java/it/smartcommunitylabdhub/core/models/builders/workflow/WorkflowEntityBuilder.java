package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WorkflowEntityBuilder implements Converter<Workflow, WorkflowEntity> {

    @Autowired
    CBORConverter cborConverter;

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build w workflow from w workflowDTO and store extra values as w cbor
     *
     * @return Workflow
     */
    public WorkflowEntity build(Workflow dto) {
        // Parse and export Spec
        Map<String, Serializable> spec = specRegistry
                .createSpec(dto.getKind(), EntityName.WORKFLOW, dto.getSpec())
                .toMap();

        // Retrieve Field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        WorkflowMetadata metadata = new WorkflowMetadata();
        metadata.configure(dto.getMetadata());

        return EntityFactory.combine(
                WorkflowEntity.builder().build(),
                builder ->
                        builder
                                // check id
                                .withIf(dto.getId() != null, e -> e.setId(dto.getId()))
                                .with(e -> e.setName(dto.getName()))
                                .with(e -> e.setKind(dto.getKind()))
                                .with(e -> e.setProject(dto.getProject()))
                                .with(e -> e.setMetadata(cborConverter.convert(dto.getMetadata())))
                                .with(e -> e.setSpec(cborConverter.convert(spec)))
                                .with(e -> e.setStatus(cborConverter.convert(dto.getStatus())))
                                .with(e -> e.setExtra(cborConverter.convert(dto.getExtra())))
                                // Store status if not present
                                .withIfElse(
                                        (statusFieldAccessor.getState() == null),
                                        (e, condition) -> {
                                            if (condition) {
                                                e.setState(State.CREATED);
                                            } else {
                                                e.setState(State.valueOf(statusFieldAccessor.getState()));
                                            }
                                        }
                                )
                                // Metadata Extraction
                                .withIfElse(
                                        metadata.getEmbedded() == null,
                                        (e, condition) -> {
                                            if (condition) {
                                                e.setEmbedded(false);
                                            } else {
                                                e.setEmbedded(metadata.getEmbedded());
                                            }
                                        }
                                )
                                .withIf(metadata.getCreated() != null, e -> e.setCreated(metadata.getCreated()))
                                .withIf(metadata.getUpdated() != null, e -> e.setUpdated(metadata.getUpdated()))
        );
    }

    @Override
    public WorkflowEntity convert(Workflow source) {
        return build(source);
    }

    /**
     * Updates a WorkflowEntity with the provided WorkflowDTO.
     *
     * @param workflow    the original WorkflowEntity to be updated
     * @param workflowDTO the new WorkflowDTO to update the WorkflowEntity with
     * @return the updated WorkflowEntity
     */

    public WorkflowEntity update(WorkflowEntity workflow, Workflow workflowDTO) {
        WorkflowEntity newWorkflow = build(workflowDTO);
        return doUpdate(workflow, newWorkflow);
    }

    /**
     * Updates the given workflow entity with the provided workflow DTO.
     *
     * @param workflow    the original workflow entity
     * @param newWorkflow the new workflow entity
     * @return the updated workflow entity
     */
    private WorkflowEntity doUpdate(WorkflowEntity workflow, WorkflowEntity newWorkflow) {
        return EntityFactory.combine(
                workflow,
                builder ->
                        builder
                                .withIfElse(
                                        newWorkflow.getState().name().equals(State.NONE.name()),
                                        (e, condition) -> {
                                            if (condition) {
                                                e.setState(State.CREATED);
                                            } else {
                                                e.setState(newWorkflow.getState());
                                            }
                                        }
                                )
                                .with(e -> e.setMetadata(newWorkflow.getMetadata()))
                                .with(e -> e.setExtra(newWorkflow.getExtra()))
                                .with(e -> e.setStatus(newWorkflow.getStatus()))
                                // Metadata Extraction
                                .withIfElse(
                                        newWorkflow.getEmbedded() == null,
                                        (e, condition) -> {
                                            if (condition) {
                                                e.setEmbedded(false);
                                            } else {
                                                e.setEmbedded(newWorkflow.getEmbedded());
                                            }
                                        }
                                )
        );
    }
}
