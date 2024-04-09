package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.models.entities.label.Label;
import it.smartcommunitylabdhub.commons.services.entities.LabelService;
import it.smartcommunitylabdhub.core.models.builders.label.LabelDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.LabelEntity;
import it.smartcommunitylabdhub.core.repositories.LabelRepository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepository repository;

    @Autowired
    private LabelDTOBuilder dtoBuilder;

    @Override
    public List<Label> findLabelsByProject(String project) {
        log.debug("find labels for project {}", project);

        return repository.findByProject(project).stream().map(e -> dtoBuilder.build(e)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Label> findLabelsByProject(String project, Pageable pageable) {
        log.debug("find labels for project {}", project);

        Page<LabelEntity> page = repository.findByProject(project, pageable);
        return new PageImpl<>(
            page.getContent().stream().map(e -> dtoBuilder.build(e)).collect(Collectors.toList()),
            pageable,
            page.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public Page<Label> searchLabels(String project, String label, Pageable pageable) {
        log.debug("find labels for project {} starting with {}", project, label);

        Page<LabelEntity> page = repository.findByProjectAndLabelStartsWithIgnoreCase(project, label, pageable);
        return new PageImpl<>(
            page.getContent().stream().map(e -> dtoBuilder.build(e)).collect(Collectors.toList()),
            pageable,
            page.getTotalElements()
        );
    }

    @Override
    public void deleteLabel(@NotNull String id) {
        log.debug("delete label with id {}", id);

        repository
            .findById(id)
            .ifPresent(entity -> {
                if (log.isTraceEnabled()) {
                    log.trace("entity: {}", entity);
                }

                repository.delete(entity);
            });
    }

    @Override
    public void deleteLabelsByProject(@NotNull String project) {
        log.debug("delete all labels for project {}", project);

        List<LabelEntity> list = repository.findByProject(project);
        repository.deleteAllInBatch(list);
    }

    @Override
    public Label findLabel(String id) {
        log.debug("get label with id {}", id);

        LabelEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }

        return dtoBuilder.build(entity);
    }

    @Override
    public Label searchLabel(String project, String label) {
        log.debug("search label with label {} for project {}", label, project);

        LabelEntity entity = repository.findByProjectAndLabelIgnoreCase(project, label.trim());
        if (entity == null) {
            return null;
        }

        return dtoBuilder.build(entity);
    }

    @Override
    public Label addLabel(String project, String label) throws DuplicatedEntityException {
        log.debug("add label {} for project {}", label, project);

        LabelEntity entity = repository.findByProjectAndLabelIgnoreCase(project, label.trim());
        if (entity != null) {
            throw new DuplicatedEntityException(label);
        }

        entity =
            LabelEntity
                .builder()
                .id(UUID.randomUUID().toString())
                .project(project)
                .label(label.toLowerCase().trim())
                .build();

        entity = repository.save(entity);

        return dtoBuilder.build(entity);
    }
}
