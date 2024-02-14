package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkflowEntityBuilder {

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build w workflow from w workflowDTO and store extra values as w cbor
     *
     * @return Workflow
     */
    public WorkflowEntity build(Workflow workflowDTO) {
        // Validate spec
        specRegistry.createSpec(workflowDTO.getKind(), EntityName.WORKFLOW, Map.of());

        // Retrieve Field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(workflowDTO.getStatus());

        // Retrieve Spec
        WorkflowBaseSpec spec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            workflowDTO.getSpec(),
            WorkflowBaseSpec.class
        );

        return EntityFactory.combine(
            WorkflowEntity.builder().build(),
            workflowDTO,
            builder ->
                builder
                    // check id
                    .withIf(workflowDTO.getId() != null, w -> w.setId(workflowDTO.getId()))
                    .with(w -> w.setName(workflowDTO.getName()))
                    .with(w -> w.setKind(workflowDTO.getKind()))
                    .with(w -> w.setProject(workflowDTO.getProject()))
                    .with(w -> w.setMetadata(ConversionUtils.convert(workflowDTO.getMetadata(), "metadata")))
                    .with(w -> w.setExtra(ConversionUtils.convert(workflowDTO.getExtra(), "cbor")))
                    .with(w -> w.setSpec(ConversionUtils.convert(spec.toMap(), "cbor")))
                    .with(w -> w.setStatus(ConversionUtils.convert(workflowDTO.getStatus(), "cbor")))
                    // Store status if not present
                    .withIfElse(
                        statusFieldAccessor.getState().equals(State.NONE.name()),
                        (w, condition) -> {
                            if (condition) {
                                w.setState(State.CREATED);
                            } else {
                                w.setState(State.valueOf(statusFieldAccessor.getState()));
                            }
                        }
                    )
                    // Metadata Extraction
                    .withIfElse(
                        workflowDTO.getMetadata().getEmbedded() == null,
                        (w, condition) -> {
                            if (condition) {
                                w.setEmbedded(false);
                            } else {
                                w.setEmbedded(workflowDTO.getMetadata().getEmbedded());
                            }
                        }
                    )
                    .withIf(
                        workflowDTO.getMetadata().getCreated() != null,
                        w -> w.setCreated(workflowDTO.getMetadata().getCreated())
                    )
                    .withIf(
                        workflowDTO.getMetadata().getUpdated() != null,
                        w -> w.setUpdated(workflowDTO.getMetadata().getUpdated())
                    )
        );
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
            newWorkflow,
            builder ->
                builder
                    .withIfElse(
                        newWorkflow.getState().name().equals(State.NONE.name()),
                        (w, condition) -> {
                            if (condition) {
                                w.setState(State.CREATED);
                            } else {
                                w.setState(newWorkflow.getState());
                            }
                        }
                    )
                    .with(w -> w.setMetadata(newWorkflow.getMetadata()))
                    .with(w -> w.setExtra(newWorkflow.getExtra()))
                    .with(w -> w.setStatus(newWorkflow.getStatus()))
                    // Metadata Extraction
                    .withIfElse(
                        newWorkflow.getEmbedded() == null,
                        (w, condition) -> {
                            if (condition) {
                                w.setEmbedded(false);
                            } else {
                                w.setEmbedded(newWorkflow.getEmbedded());
                            }
                        }
                    )
        );
    }
}
