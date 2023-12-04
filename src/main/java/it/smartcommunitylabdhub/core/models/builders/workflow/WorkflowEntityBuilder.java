package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.WorkflowFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.specs.WorkflowBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkflowEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build w workflow from w workflowDTO and store extra values as w cbor
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
                        EntityName.WORKFLOW,
                        JacksonMapper.objectMapper.convertValue(workflowDTO,
                                JacksonMapper.typeRef));


        // Retrieve Spec
        WorkflowBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(workflowDTO.getSpec(), WorkflowBaseSpec.class);

        return EntityFactory.combine(
                ConversionUtils.convert(workflowDTO, "workflow"), workflowDTO, builder -> builder
                        // check id
                        .withIfElse(workflowDTO.getId() != null &&
                                        workflowDTO.getMetadata().getVersion() != null,
                                (w) -> {
                                    if (workflowDTO.getId()
                                            .equals(workflowDTO.getMetadata().getVersion())) {
                                        w.setId(workflowDTO.getMetadata().getVersion());
                                    } else {
                                        throw new CoreException(
                                                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                                                "Trying to store item with which has different signature <id != version>",
                                                HttpStatus.INTERNAL_SERVER_ERROR
                                        );
                                    }
                                },
                                (w) -> {
                                    if (workflowDTO.getId() == null &&
                                            workflowDTO.getMetadata().getVersion() != null) {
                                        w.setId(workflowDTO.getMetadata().getVersion());
                                    } else {
                                        w.setId(workflowDTO.getId());
                                    }
                                })
                        .with(p -> p.setMetadata(ConversionUtils.convert(
                                workflowDTO.getMetadata(), "metadata")))
                        .with(w -> w.setExtra(ConversionUtils.convert(
                                workflowDTO.getExtra(), "cbor")))
                        .with(w -> w.setSpec(ConversionUtils.convert(
                                spec.toMap(), "cbor")))
                        .with(w -> w.setStatus(ConversionUtils.convert(
                                workflowDTO.getStatus(), "cbor")))

                        // Store status if not present
                        .withIfElse(workflowFieldAccessor.getState().equals(State.NONE.name()),
                                (w, condition) -> {
                                    if (condition) {
                                        w.setState(State.CREATED);
                                    } else {
                                        w.setState(State.valueOf(workflowFieldAccessor.getState()));
                                    }
                                }
                        )

                        // Metadata Extraction
                        .withIfElse(workflowDTO.getMetadata().getEmbedded() == null,
                                (w, condition) -> {
                                    if (condition) {
                                        w.setEmbedded(false);
                                    } else {
                                        w.setEmbedded(workflowDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
                        .withIf(workflowDTO.getMetadata().getCreated() != null, (w) ->
                                w.setCreated(workflowDTO.getMetadata().getCreated()))
                        .withIf(workflowDTO.getMetadata().getUpdated() != null, (w) ->
                                w.setUpdated(workflowDTO.getMetadata().getUpdated()))
        );

    }

    /**
     * Update w workflow if element is not passed it override causing empty field
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
                                (w, condition) -> {
                                    if (condition) {
                                        w.setState(State.CREATED);
                                    } else {
                                        w.setState(State.valueOf(workflowFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(w -> w.setMetadata(ConversionUtils.convert(
                                workflowDTO.getMetadata(), "metadata")))
                        .with(w -> w.setExtra(ConversionUtils.convert(
                                workflowDTO.getExtra(), "cbor")))
                        .with(w -> w.setStatus(ConversionUtils.convert(
                                workflowDTO.getStatus(), "cbor")))

                        // Metadata Extraction
                        .withIfElse(workflowDTO.getMetadata().getEmbedded() == null,
                                (w, condition) -> {
                                    if (condition) {
                                        w.setEmbedded(false);
                                    } else {
                                        w.setEmbedded(workflowDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
        );
    }
}
