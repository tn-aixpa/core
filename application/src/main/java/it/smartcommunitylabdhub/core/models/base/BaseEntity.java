package it.smartcommunitylabdhub.core.models.base;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public interface BaseEntity extends Serializable {
    String getId();

    @NotNull
    String getName();

    @NotNull
    String getKind();

    String getProject();

    Date getCreated();

    Date getUpdated();

    String getCreatedBy();

    String getUpdatedBy();

    //always store metadata
    //TODO evaluate splitting

    byte[] getMetadata();

    void setMetadata(byte[] metadata);
}
