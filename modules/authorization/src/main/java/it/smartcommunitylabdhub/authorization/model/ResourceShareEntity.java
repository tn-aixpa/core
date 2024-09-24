package it.smartcommunitylabdhub.authorization.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EntityListeners({ AuditingEntityListener.class })
@Table(name = "resource_shares")
public class ResourceShareEntity {

    @Id
    private String id;

    @NotNull
    @Column(name = "project", updatable = false)
    private String project;

    @NotNull
    @Column(name = "entity_type", updatable = false)
    private String entity;

    @NotNull
    @Column(name = "entity_id", updatable = false)
    private String entityId;

    @CreatedBy
    @Column(name = "owner", updatable = false)
    private String owner;

    @NotNull
    @Column(name = "user", updatable = false)
    private String user;

    @CreatedDate
    @Column(name = "issued_at", updatable = false)
    private Date issuedTime;

    @Column(name = "expires_at")
    private Date expirationTime;
}
