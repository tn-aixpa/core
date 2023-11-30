package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;

@ConverterType(type = "workflow")
public class WorkflowConverter implements Converter<Workflow, WorkflowEntity> {

    @Override
    public WorkflowEntity convert(Workflow workflowDTO) throws CustomException {
        return WorkflowEntity.builder()
                .id(workflowDTO.getId())
                .name(workflowDTO.getName())
                .kind(workflowDTO.getKind())
                .project(workflowDTO.getProject())
                .embedded(workflowDTO.getEmbedded())
                .build();
    }

    @Override
    public Workflow reverseConvert(WorkflowEntity workflow) throws CustomException {
        return Workflow.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .kind(workflow.getKind())
                .project(workflow.getProject())
                .embedded(workflow.getEmbedded())
                .created(workflow.getCreated())
                .updated(workflow.getUpdated())
                .build();
    }

}
