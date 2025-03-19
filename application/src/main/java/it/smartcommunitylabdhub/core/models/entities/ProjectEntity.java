package it.smartcommunitylabdhub.core.models.entities;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import it.smartcommunitylabdhub.core.models.base.SpecEntity;
import it.smartcommunitylabdhub.core.models.base.StatusEntity;
import it.smartcommunitylabdhub.core.models.converters.StateStringAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.sql.Types;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "projects")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@EntityListeners({ AuditingEntityListener.class })
public class ProjectEntity implements BaseEntity, SpecEntity, StatusEntity {

    @Id
    @Column(unique = true, updatable = false)
    private String id;

    @Column(nullable = false, updatable = false)
    protected String kind;

    @Column(unique = true, updatable = false)
    private String name;

    private String source;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    private byte[] metadata;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    private byte[] spec;

    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    private byte[] status;

    @CreatedDate
    @Column(updatable = false)
    private Date created;

    @LastModifiedDate
    private Date updated;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    protected String updatedBy;

    @Convert(converter = StateStringAttributeConverter.class)
    private State state;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.id = this.name;
        }
    }

    @Override
    public String getProject() {
        return name;
    }

    @Override
    public @NotNull String getKind() {
        return "project";
    }
}
