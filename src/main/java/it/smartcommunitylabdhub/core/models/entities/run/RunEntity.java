package it.smartcommunitylabdhub.core.models.entities.run;

import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "runs")
public class RunEntity implements BaseEntity {

    @Id
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    // COMMENT: {kind}+{action}://{project_name}/{function_name}:{version(uuid)} action can be
    // 'build', 'other...'
    private String task;

    @Column(nullable = false)
    private String kind;

    @Column(nullable = false)
    private String project;

    @Column(nullable = false, name = "task_id")
    private String taskId;

    @Lob
    private byte[] metadata;

    @Lob
    private byte[] spec;

    @Lob
    private byte[] extra;

    @CreationTimestamp
    @Column(updatable = false)
    private Date created;

    @UpdateTimestamp
    private Date updated;

    @Enumerated(EnumType.STRING)
    private RunState state;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
