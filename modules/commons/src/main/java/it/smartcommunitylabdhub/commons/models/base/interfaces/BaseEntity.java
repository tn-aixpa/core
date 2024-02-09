package it.smartcommunitylabdhub.commons.models.base.interfaces;

import java.io.Serializable;

/**
 * This baseEntity interface should be implemented by all Entity that need for instance to receive
 * for instance a correct service based on the kind value.
 * <p>
 * es: for kind = etl the Entity will receive the EtlService
 */
public interface BaseEntity extends Serializable {

}
