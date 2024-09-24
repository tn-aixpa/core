package it.smartcommunitylabdhub.authorization.services;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/*
 * Helper service to aid authorization management over entities.
 * Could leverage cache on non-updatable fields
 */
public interface AuthorizableAwareEntityService<T extends BaseDTO> {
    /**
     * Find identifiers of entities via createdBy
     * @param createdBy
     * @return
     */
    List<String> findIdsByCreatedBy(@NotNull String createdBy);

    /**
     * Find identifiers of entities via updatedBy
     * @param updatedBy
     * @return
     */
    List<String> findIdsByUpdatedBy(@NotNull String updatedBy);

    /**
     * Find identifiers of entities via project
     * @param project
     * @return
     */
    List<String> findIdsByProject(@NotNull String project);

    /**
     * Find identifiers of entities via share
     * @param createdBy
     * @return
     */
    List<String> findIdsBySharedTo(@NotNull String user);

    /**
     * Find names of entities via createdBy
     * @param createdBy
     * @return
     */
    List<String> findNamesByCreatedBy(@NotNull String createdBy);

    /**
     * Find names of entities via updatedBy
     * @param updatedBy
     * @return
     */
    List<String> findNamesByUpdatedBy(@NotNull String updatedBy);

    /**
     * Find names of entities via project
     * @param project
     * @return
     */
    List<String> findNamesByProject(@NotNull String project);

    /**
     * Find names of entities via share
     * @param createdBy
     * @return
     */
    List<String> findNamesBySharedTo(@NotNull String user);
}
