package it.smartcommunitylabdhub.commons.models.base;

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
    byte[] getSpec();
    byte[] getStatus();
    byte[] getExtra();

    Date getCreated();
    Date getUpdated();

    void setMetadata(byte[] metadata);
    void setSpec(byte[] spec);
    void setStatus(byte[] status);
    void setExtra(byte[] extra);
}
