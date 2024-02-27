package it.smartcommunitylabdhub.core.models.entities.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.components.cloud.listeners.TaskSavedListener;
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
import jakarta.persistence.UniqueConstraint;
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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tasks", uniqueConstraints = { @UniqueConstraint(columnNames = { "function", "kind" }) })
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@EntityListeners({ AuditingEntityListener.class, TaskSavedListener.class })
public class TaskEntity implements BaseEntity {

    @Id
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    // COMMENT: {function_kind}://{project}/{function}:{version}
    private String function;

    @Column(nullable = false)
    private String kind;

    @Column(nullable = false)
    private String project;

    @Lob
    private byte[] metadata;

    @Lob
    private byte[] spec;

    @Lob
    private byte[] extra;

    @Lob
    private byte[] status;

    @CreatedDate
    @Column(updatable = false)
    private Date created;

    @LastModifiedDate
    private Date updated;

    @Enumerated(EnumType.STRING)
    private State state;

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
}
