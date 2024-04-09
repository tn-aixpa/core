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
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "logs")
public class LogEntity extends AbstractEntity {

    @Column(nullable = false)
    private String run;

    @Lob
    private byte[] body;

    @Lob
    private byte[] extra;

    private State state;

    @Override
    public @NotNull String getName() {
        return id;
    }

    @Override
    public @NotNull String getKind() {
        return "log";
    }
}
