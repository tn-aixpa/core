package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.label.Label;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    public List<Label> findLabelsByProject(@NotNull String project) throws SystemException;

    public Page<Label> findLabelsByProject(@NotNull String project, Pageable pageable) throws SystemException;

    public Page<Label> searchLabels(@NotNull String project, @NotNull String label, Pageable pageable)
        throws SystemException;

    public Label findLabel(@NotNull String id) throws SystemException;

    public Label searchLabel(@NotNull String project, @NotNull String label) throws SystemException;

    public Label addLabel(@NotNull String project, @NotNull String label)
        throws DuplicatedEntityException, SystemException;

    public void deleteLabel(@NotNull String id) throws SystemException;

    public void deleteLabelsByProject(@NotNull String project) throws SystemException;
}
