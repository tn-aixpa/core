package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.function.FunctionDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.secret.SecretDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowDTOBuilder;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    SecretDTOBuilder secretDTOBuilder;

    @Autowired
    CBORConverter cborConverter;

    public Project build(
        ProjectEntity project,
        List<ArtifactEntity> artifacts,
        List<FunctionEntity> functions,
        List<WorkflowEntity> workflows,
        List<DataItemEntity> dataItems,
        List<SecretEntity> secrets,
        boolean embeddable
    ) {
        // Retrieve spec
        Map<String, Serializable> spec = cborConverter.reverseConvert(project.getSpec());
        spec.put(
            "functions",
            functions
                .stream()
                .map(f -> functionDTOBuilder.build(f, embeddable))
                .collect(Collectors.toCollection(ArrayList::new))
        );
        spec.put(
            "artifacts",
            artifacts
                .stream()
                .map(a -> artifactDTOBuilder.build(a, embeddable))
                .collect(Collectors.toCollection(ArrayList::new))
        );
        spec.put(
            "workflows",
            workflows
                .stream()
                .map(w -> workflowDTOBuilder.build(w, embeddable))
                .collect(Collectors.toCollection(ArrayList::new))
        );
        spec.put(
            "dataitems",
            dataItems
                .stream()
                .map(d -> dataItemDTOBuilder.build(d, embeddable))
                .collect(Collectors.toCollection(ArrayList::new))
        );
        spec.put(
            "secrets",
            secrets
                .stream()
                .map(s -> secretDTOBuilder.build(s, embeddable))
                .collect(Collectors.toCollection(ArrayList::new))
        );

        // Find base run spec
        return EntityFactory.create(
            Project::new,
            project,
            builder ->
                builder
                    .with(dto -> dto.setId(project.getId()))
                    .with(dto -> dto.setName(project.getName()))
                    .with(dto -> dto.setKind(project.getKind()))
                    .with(dto -> {
                        //read metadata as-is
                        Map<String, Serializable> meta = cborConverter.reverseConvert(project.getMetadata());

                        // Set Metadata for project
                        ProjectMetadata metadata = new ProjectMetadata();
                        metadata.configure(meta);

                        if (!StringUtils.hasText(metadata.getName())) {
                            metadata.setName(project.getName());
                        }
                        metadata.setProject(project.getName());
                        metadata.setDescription(project.getDescription());
                        metadata.setSource(project.getSource());
                        metadata.setCreated(project.getCreated());
                        metadata.setUpdated(project.getUpdated());

                        //merge into map with override
                        dto.setMetadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()));
                    })
                    .with(dto -> dto.setSpec(spec))
                    .with(dto -> dto.setExtra(cborConverter.reverseConvert(project.getExtra())))
                    .with(dto ->
                        dto.setStatus(
                            MapUtils.mergeMultipleMaps(
                                cborConverter.reverseConvert(project.getStatus()),
                                Map.of("state", project.getState())
                            )
                        )
                    )
        );
    }
}
