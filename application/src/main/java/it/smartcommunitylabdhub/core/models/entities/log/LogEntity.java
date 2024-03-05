package it.smartcommunitylabdhub.core.models.entities.log;

import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "logs")
@EntityListeners(AuditingEntityListener.class)
public class LogEntity implements BaseEntity {

    @Id
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    private String project;

    @Column(nullable = false)
    private String run;

    @Lob
    private byte[] body;

    @Lob
    private byte[] extra;

    @Lob
    private byte[] status;

    @Lob
    private byte[] metadata;

    @Enumerated(EnumType.STRING)
    private State state;

    @CreatedDate
    @Column(updatable = false)
    private Date created;

    @LastModifiedDate
    private Date updated;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Override
    public @NotNull String getName() {
        return id;
    }

    @Override
    public @NotNull String getKind() {
        return "log";
    }

    @Override
    public byte[] getSpec() {
        return null;
    }

    @Override
    public void setSpec(byte[] spec) {}
}
