package it.smartcommunitylabdhub.core.models.entities;

import it.smartcommunitylabdhub.core.models.base.SpecEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
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
@Table(
    name = "tasks",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "function", "kind" }),
        @UniqueConstraint(columnNames = { "workflow", "kind" }),
    }
)
public class TaskEntity extends AbstractEntity implements SpecEntity {

    @Column(nullable = true)
    private String function;

    @Column(nullable = true)
    private String workflow;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    protected byte[] spec;

    @Override
    public @NotNull String getName() {
        return id;
    }

    @Override
    public void setName(String name) {
        //not available
    }
}
