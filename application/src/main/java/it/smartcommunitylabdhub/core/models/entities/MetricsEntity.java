package it.smartcommunitylabdhub.core.models.entities;

import java.io.Serializable;
import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(
    name = "metrics",
    indexes = { @Index(name = "metrics_type_id_index", columnList = "entityName, entityId", unique = false) }
)
public class MetricsEntity implements Serializable {

    @Id
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false)
    private String name;
    
    @JdbcTypeCode(Types.LONGVARBINARY)
    @ToString.Exclude
    private byte[] data;
}
