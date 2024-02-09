package it.smartcommunitylabdhub.core.models.entities.project;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import it.smartcommunitylabdhub.commons.models.base.interfaces.BaseEntity;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.components.cloud.listeners.EntitySavedListener;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "projects")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@EntityListeners({AuditingEntityListener.class, EntitySavedListener.class})
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
            this.id = this.name;
        }
    }
}
