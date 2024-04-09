package it.smartcommunitylabdhub.core.models.entities;

import it.smartcommunitylabdhub.commons.models.enums.State;
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
@ToString
@Entity
@Table(name = "runs")
public class RunEntity extends AbstractEntity {

    @Column(nullable = false)
    // COMMENT: {kind}+{action}://{project_name}/{function_name}:{version(uuid)} action can be
    // 'build', 'other...'
    private String task;

    @Lob
    @ToString.Exclude
    private byte[] extra;

    private State state;

    @Override
    public @NotNull String getName() {
        return id;
    }
}
