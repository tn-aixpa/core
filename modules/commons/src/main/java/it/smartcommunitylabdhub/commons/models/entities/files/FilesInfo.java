package it.smartcommunitylabdhub.commons.models.entities.files;

<<<<<<< HEAD
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import it.smartcommunitylabdhub.commons.models.base.FileInfo;
=======
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import java.util.List;
>>>>>>> origin/file_info_repo
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonPropertyOrder(alphabetic = true)
public class FilesInfo {
<<<<<<< HEAD
=======

>>>>>>> origin/file_info_repo
    private String id;

    private String entityName;

    private String entityId;
<<<<<<< HEAD
    
    private List<FileInfo> files;

=======

    private List<FileInfo> files;
>>>>>>> origin/file_info_repo
}
