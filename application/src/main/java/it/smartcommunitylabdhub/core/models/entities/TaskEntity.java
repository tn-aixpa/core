package it.smartcommunitylabdhub.core.models.entities;

import it.smartcommunitylabdhub.core.models.base.SpecEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
@Entity
@Table(name = "tasks", uniqueConstraints = { @UniqueConstraint(columnNames = { "function", "kind" }) })
public class TaskEntity extends AbstractEntity implements SpecEntity {

    @Column(nullable = false)
    // COMMENT: {function_kind}://{project}/{function}:{version}
    private String function;

    @Lob
    @ToString.Exclude
    protected byte[] spec;

    @Override
    public @NotNull String getName() {
        return id;
    }
}
