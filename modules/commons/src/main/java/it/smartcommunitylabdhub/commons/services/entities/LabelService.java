package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.models.entities.label.Label;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    public List<Label> findLabelsByProject(@NotNull String project);

    public Page<Label> findLabelsByProject(@NotNull String project, Pageable pageable);

    public Page<Label> searchLabels(@NotNull String project, @NotNull String label, Pageable pageable);

    public Label findLabel(@NotNull String id);

    public Label searchLabel(@NotNull String project, @NotNull String label);

    public Label addLabel(@NotNull String project, @NotNull String label) throws DuplicatedEntityException;

    public void deleteLabel(@NotNull String id);

    public void deleteLabelsByProject(@NotNull String project);
}
