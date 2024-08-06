package it.smartcommunitylabdhub.core.models.entities;

<<<<<<< HEAD
import java.io.Serializable;

=======
>>>>>>> origin/file_info_repo
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
<<<<<<< HEAD
=======
import java.io.Serializable;
>>>>>>> origin/file_info_repo
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
<<<<<<< HEAD
@Entity
@Table(
    name = "filesinfo",
    indexes = {
    		@Index(name = "files_name_id_index", columnList = "entityName, entityId", unique = true),
    }
=======
@ToString
@Entity
@Table(
    name = "filesinfo",
    indexes = { @Index(name = "files_name_id_index", columnList = "entityName, entityId", unique = true) }
>>>>>>> origin/file_info_repo
)
public class FilesInfoEntity implements Serializable {

    @Id
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private String entityId;
<<<<<<< HEAD
    
=======

>>>>>>> origin/file_info_repo
    @Lob
    @ToString.Exclude
    private byte[] files;
}
