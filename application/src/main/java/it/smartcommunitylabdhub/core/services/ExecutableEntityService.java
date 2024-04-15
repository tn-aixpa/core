package it.smartcommunitylabdhub.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.schemas.Schema;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import it.smartcommunitylabdhub.core.models.entities.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.WorkflowEntity;

@Component
public class ExecutableEntityService {
    
    @Autowired
    private EntityService<Function, FunctionEntity> functionEntityService;
    @Autowired
    private EntityService<Workflow, WorkflowEntity> workflowEntityService;
    @Autowired
    private SpecRegistry specRegistry;


    public EntityService<? extends Executable, ? extends BaseEntity> getEntityServiceByRuntime(String runtime) {
        try {
            // throws exception if not found
            specRegistry.getSchema(runtime, EntityName.FUNCTION);
            return functionEntityService;
        } catch (Exception e) {
            // throws exception if not found
            specRegistry.getSchema(runtime, EntityName.WORKFLOW);            
            return workflowEntityService;
        }
    }

    public EntityService<? extends Executable, ? extends BaseEntity> getEntityServiceByEntity(EntityName entity) {
        return EntityName.FUNCTION.equals(entity) 
        ? functionEntityService
        : workflowEntityService;
    }

    public EntityName getEntityNameByRuntime(String runtime) {
        try {
            // throws exception if not found
            specRegistry.getSchema(runtime, EntityName.FUNCTION);
            return EntityName.FUNCTION;
        } catch (Exception e) {
            // throws exception if not found
            specRegistry.getSchema(runtime, EntityName.WORKFLOW);            
            return EntityName.WORKFLOW;
        }

    }

}
