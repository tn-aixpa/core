package it.smartcommunitylabdhub.core.models.base;

import it.smartcommunitylabdhub.commons.models.enums.State;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * This baseEntity interface should be implemented by all Entity that need for instance to receive
 * for instance a correct service based on the kind value.
 * <p>
 * es: for kind = etl the Entity will receive the EtlService
 */
public interface BaseEntity extends Serializable {
    String getId();

    @NotNull
    String getName();

    @NotNull
    String getKind();

    String getProject();

    byte[] getMetadata();

    void setMetadata(byte[] metadata);

    byte[] getSpec();

    void setSpec(byte[] spec);

    byte[] getStatus();

    void setStatus(byte[] status);

    byte[] getExtra();

    void setExtra(byte[] extra);

    State getState();

    void setState(State state);

    Date getCreated();

    Date getUpdated();
}
