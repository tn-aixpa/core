package it.smartcommunitylabdhub.core.models.entities.log;

import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import it.smartcommunitylabdhub.commons.models.enums.State;
import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;
import lombok.*;
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
}
