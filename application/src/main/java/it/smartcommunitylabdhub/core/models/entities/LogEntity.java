package it.smartcommunitylabdhub.core.models.entities;

import it.smartcommunitylabdhub.core.models.base.SpecEntity;
import it.smartcommunitylabdhub.core.models.base.StatusEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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

    @Lob
    private byte[] content;

    @Lob
    @ToString.Exclude
    protected byte[] spec;

    @Lob
    @ToString.Exclude
    protected byte[] status;

    @Override
    public @NotNull String getName() {
        return id;
    }

    @Override
    public @NotNull String getKind() {
        return "log";
    }
}
