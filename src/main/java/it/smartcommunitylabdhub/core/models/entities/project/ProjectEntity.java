package it.smartcommunitylabdhub.core.models.entities.project;

import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;
import it.smartcommunitylabdhub.core.models.enums.State;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "projects")
public class ProjectEntity implements BaseEntity {

    @Id
    @Column(unique = true)
    private String id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private String kind;

    private String description;

    private String source;

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
    private State state;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
