package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.specs.WorkflowBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkflowEntityBuilder {


    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    /**
     * Build a workflow from a workflowDTO and store extra values as a cbor
     *
     * @return Workflow
     */
    public WorkflowEntity build(Workflow workflowDTO) {

        specRegistry.createSpec(workflowDTO.getKind(), SpecEntity.WORKFLOW, Map.of());

        // Retrieve Spec
        WorkflowBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(workflowDTO.getSpec(), WorkflowBaseSpec.class);

        return EntityFactory.combine(
                ConversionUtils.convert(workflowDTO, "workflow"), workflowDTO,
                builder -> builder
                        .with(w -> w.setMetadata(
                                ConversionUtils.convert(workflowDTO
                                                .getMetadata(),
                                        "metadata")))

                        .with(w -> w.setExtra(
                                ConversionUtils.convert(workflowDTO
                                                .getExtra(),

                                        "cbor")))
                        .with(w -> w.setSpec(
                                ConversionUtils.convert(spec.toMap(),
                                        "cbor"))));

    }

    /**
     * Update a workflow if element is not passed it override causing empty field
     *
     * @param workflow
     * @return
     */
    public WorkflowEntity update(WorkflowEntity workflow, Workflow workflowDTO) {

        WorkflowBaseSpec spec = JacksonMapper.objectMapper
                .convertValue(workflowDTO.getSpec(), WorkflowBaseSpec.class);


        return EntityFactory.combine(
                workflow, workflowDTO, builder -> builder
                        .with(w -> w.setState(workflowDTO.getState() == null
                                ? State.CREATED
                                : State.valueOf(workflowDTO
                                .getState())))
                        .with(w -> w.setMetadata(
                                ConversionUtils.convert(workflowDTO
                                                .getMetadata(),
                                        "metadata")))
                        .with(w -> w.setExtra(
                                ConversionUtils.convert(workflowDTO
                                                .getExtra(),
                                        "cbor")))
                        .with(w -> w.setSpec(
                                ConversionUtils.convert(spec.toMap(),
                                        "cbor")))
                        .with(w -> w.setEmbedded(
                                workflowDTO.getEmbedded())));
    }
}
