package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.WorkflowFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.specs.WorkflowBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkflowEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build a workflow from a workflowDTO and store extra values as a cbor
     *
     * @return Workflow
     */
    public WorkflowEntity build(Workflow workflowDTO) {

        // Validate spec
        specRegistry.createSpec(workflowDTO.getKind(), EntityName.WORKFLOW, Map.of());

        // Retrieve Field accessor
        WorkflowFieldAccessor<?> workflowFieldAccessor =
                accessorRegistry.createAccessor(
                        workflowDTO.getKind(),
                        EntityName.FUNCTION,
                        JacksonMapper.objectMapper.convertValue(workflowDTO,
                                JacksonMapper.typeRef));


        // Retrieve Spec
        WorkflowBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(workflowDTO.getSpec(), WorkflowBaseSpec.class);

        return EntityFactory.combine(
                ConversionUtils.convert(workflowDTO, "workflow"), workflowDTO,
                builder -> builder
                        .withIfElse(workflowFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        workflowFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        workflowFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(workflowFieldAccessor.getState()));
                                    }
                                }
                        )
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
     * @param workflow Workflow
     * @return WorkflowEntity
     */
    public WorkflowEntity update(WorkflowEntity workflow, Workflow workflowDTO) {

        // Validate Spec
        specRegistry.createSpec(workflowDTO.getKind(), EntityName.WORKFLOW, Map.of());

        // Retrieve Field accessor
        WorkflowFieldAccessor<?> workflowFieldAccessor =
                accessorRegistry.createAccessor(
                        workflowDTO.getKind(),
                        EntityName.WORKFLOW,
                        JacksonMapper.objectMapper.convertValue(workflowDTO,
                                JacksonMapper.typeRef));


        return EntityFactory.combine(
                workflow, workflowDTO, builder -> builder
                        .withIfElse(workflowFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        workflowFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        workflowFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(workflowFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(w -> w.setMetadata(
                                ConversionUtils.convert(workflowDTO
                                                .getMetadata(),
                                        "metadata")))
                        .with(w -> w.setExtra(
                                ConversionUtils.convert(workflowDTO
                                                .getExtra(),
                                        "cbor")))
                        .with(w -> w.setEmbedded(
                                workflowDTO.getEmbedded())));
    }
}
