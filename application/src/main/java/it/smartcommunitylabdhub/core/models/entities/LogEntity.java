package it.smartcommunitylabdhub.core.models.entities;

import it.smartcommunitylabdhub.core.models.base.SpecEntity;
import it.smartcommunitylabdhub.core.models.base.StatusEntity;
import it.smartcommunitylabdhub.core.models.persistence.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.sql.Types;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "logs")
public class LogEntity extends AbstractEntity implements SpecEntity, StatusEntity {

    @Column(nullable = false)
    private String run;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    private byte[] content;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    protected byte[] spec;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    protected byte[] status;

    @Override
    public @NotNull String getName() {
        return id;
    }

    @Override
    public void setName(String name) {
        //not available
    }

    @Override
    public @NotNull String getKind() {
        return "log";
    }
}
