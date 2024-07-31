package it.smartcommunitylabdhub.commons.models.entities.files;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import it.smartcommunitylabdhub.commons.models.base.FileInfo;
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
    private String id;

    private String entityName;

    private String entityId;
    
    private List<FileInfo> files;

}
