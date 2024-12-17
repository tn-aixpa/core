package it.smartcommunitylabdhub.core.models.entities;

import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.base.SpecEntity;
import it.smartcommunitylabdhub.core.models.base.StatusEntity;
import it.smartcommunitylabdhub.core.models.converters.types.StateStringAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.sql.Types;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
@Entity
@Table(name = "secrets", uniqueConstraints = { @UniqueConstraint(columnNames = { "project", "name" }) })
public class SecretEntity extends AbstractEntity implements SpecEntity, StatusEntity {

    @Column(nullable = false)
    private String name;

    private Boolean embedded;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    protected byte[] spec;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    protected byte[] status;

    @Convert(converter = StateStringAttributeConverter.class)
    private State state;
}
