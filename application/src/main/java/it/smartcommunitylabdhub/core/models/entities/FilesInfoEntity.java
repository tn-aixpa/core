package it.smartcommunitylabdhub.core.models.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
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
@Entity
@Table(
    name = "filesinfo",
    indexes = {
    		@Index(name = "files_name_id_index", columnList = "entityName, entityId", unique = true),
    }
)
public class FilesInfoEntity implements Serializable {

    @Id
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private String entityId;
    
    @Lob
    @Column(columnDefinition="BLOB")
    @ToString.Exclude
    private byte[] files;
}
