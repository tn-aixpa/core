package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.function.FunctionDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowDTOBuilder;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.project.Project;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.project.metadata.ProjectMetadata;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProjectDTOBuilder {

    @Autowired
    ArtifactDTOBuilder artifactDTOBuilder;

    @Autowired
    FunctionDTOBuilder functionDTOBuilder;

    @Autowired
    WorkflowDTOBuilder workflowDTOBuilder;

    @Autowired
    DataItemDTOBuilder dataItemDTOBuilder;

    @Autowired
    MetadataConverter<ProjectMetadata> metadataConverter;

    public Project build(
            ProjectEntity project,
            List<ArtifactEntity> artifacts,
            List<FunctionEntity> functions,
            List<WorkflowEntity> workflows,
            List<DataItemEntity> dataItems,
            boolean embeddable) {

        // Retrieve spec
        Map<String, Object> spec = ConversionUtils.reverse(
                project.getSpec(), "cbor");
        spec.put("functions",
                functions.stream()
                        .map(f -> functionDTOBuilder.build(
                                f, embeddable))
                        .collect(Collectors.toList()));
        spec.put("artifacts",
                artifacts.stream()
                        .map(a -> artifactDTOBuilder.build(
                                a,
                                embeddable))
                        .collect(Collectors.toList()));
        spec.put("workflows",
                workflows.stream()
                        .map(w -> workflowDTOBuilder.build(
                                w, embeddable))
                        .collect(Collectors.toList()));
        spec.put("dataitems",
                dataItems.stream()
                        .map(d -> dataItemDTOBuilder.build(
                                d, embeddable))
                        .collect(Collectors.toList()));

        // Find base run spec
        return EntityFactory.create(Project::new, project, builder -> builder
                .with(dto -> dto.setId(project.getId()))
                .with(dto -> dto.setName(project.getName()))
                .with(dto -> dto.setKind(project.getKind()))
                .with(dto -> dto.setDescription(project.getDescription()))
                .with(dto -> dto.setSource(project.getSource()))
                .with(dto -> dto.setExtra(ConversionUtils.reverse(
                        project.getExtra(),
                        "cbor")))
                .with(dto -> dto.setSpec(spec))
                .with(dto -> dto.setMetadata(Optional
                        .ofNullable(
                                metadataConverter.reverseByClass(
                                        project.getMetadata(),
                                        ProjectMetadata.class))
                        .orElseGet(ProjectMetadata::new)))
                .with(dto -> dto.setStatus(
                        ConversionUtils.reverse(
                                project.getStatus(), "cbor")))
                .with(dto -> dto.setCreated(project.getCreated()))
                .with(dto -> dto.setUpdated(project.getUpdated()))


        );
    }
}
