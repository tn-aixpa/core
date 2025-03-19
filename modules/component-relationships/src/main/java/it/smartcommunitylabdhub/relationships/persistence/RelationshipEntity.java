package it.smartcommunitylabdhub.relationships.persistence;

import it.smartcommunitylabdhub.commons.models.enums.RelationshipName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
@Entity
@Table(
    name = "relationships",
    indexes = {
        @Index(name = "relationships_project_index", columnList = "project"),
        @Index(name = "relationships_source_index", columnList = "sourceId"),
        @Index(name = "relationships_dest_index", columnList = "destId"),
    }
)
public class RelationshipEntity {

    @Id
    @Column(unique = true, updatable = false)
    private String id;

    @Column(nullable = false)
    private RelationshipName relationship;

    @Column(nullable = false, updatable = false)
    private String project;

    @Column(nullable = false)
    private String sourceType;

    @Column(nullable = false)
    private String sourceId;

    @Column(nullable = false)
    private String sourceKey;

    @Column(nullable = false)
    private String destType;

    @Column(nullable = false)
    private String destId;

    @Column(nullable = false)
    private String destKey;

    @LastModifiedDate
    private Date updated;
}
