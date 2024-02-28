package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.repositories.ArtifactRepository;
import it.smartcommunitylabdhub.core.repositories.DataItemRepository;
import it.smartcommunitylabdhub.core.repositories.FunctionRepository;
import it.smartcommunitylabdhub.core.repositories.LogRepository;
import it.smartcommunitylabdhub.core.repositories.ProjectRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.repositories.SecretRepository;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import it.smartcommunitylabdhub.core.repositories.WorkflowRepository;
import it.smartcommunitylabdhub.core.services.BaseEntityServiceImpl;
import it.smartcommunitylabdhub.core.services.EntityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class PersistenceConfig {

    @Bean
    public EntityService<Artifact, ArtifactEntity> artifactEntityService(
        ArtifactRepository repository,
        Converter<Artifact, ArtifactEntity> entityBuilder,
        Converter<ArtifactEntity, Artifact> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }

    @Bean
    public EntityService<DataItem, DataItemEntity> dataItemEntityService(
        DataItemRepository repository,
        Converter<DataItem, DataItemEntity> entityBuilder,
        Converter<DataItemEntity, DataItem> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }

    @Bean
    public EntityService<Function, FunctionEntity> functionEntityService(
        FunctionRepository repository,
        Converter<Function, FunctionEntity> entityBuilder,
        Converter<FunctionEntity, Function> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }

    @Bean
    public EntityService<Log, LogEntity> logEntityService(
        LogRepository repository,
        Converter<Log, LogEntity> entityBuilder,
        Converter<LogEntity, Log> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }

    // @Bean
    // public EntityService<Project, ProjectEntity> projectEntityService(
    //     ProjectRepository repository,
    //     Converter<Project, ProjectEntity> entityBuilder,
    //     Converter<ProjectEntity, Project> dtoBuilder
    // ) {
    //     return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    // }

    @Bean
    public EntityService<Run, RunEntity> runEntityService(
        RunRepository repository,
        Converter<Run, RunEntity> entityBuilder,
        Converter<RunEntity, Run> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }

    @Bean
    public EntityService<Secret, SecretEntity> secretEntityService(
        SecretRepository repository,
        Converter<Secret, SecretEntity> entityBuilder,
        Converter<SecretEntity, Secret> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }

    @Bean
    public EntityService<Task, TaskEntity> taskEntityService(
        TaskRepository repository,
        Converter<Task, TaskEntity> entityBuilder,
        Converter<TaskEntity, Task> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }

    @Bean
    public EntityService<Workflow, WorkflowEntity> workflowEntityService(
        WorkflowRepository repository,
        Converter<Workflow, WorkflowEntity> entityBuilder,
        Converter<WorkflowEntity, Workflow> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }
}
