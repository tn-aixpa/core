package it.smartcommunitylabdhub.commons.models.base;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD

=======
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
>>>>>>> origin/file_info_repo
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class FileInfo implements Serializable {

    private String path;
<<<<<<< HEAD
    
    @JsonProperty("src_path")
    private String sourcePath;
    
=======

    @JsonProperty("src_path")
    private String sourcePath;

>>>>>>> origin/file_info_repo
    private String name;

    @JsonProperty("content_type")
    private String contentType;

    private long size;

    @JsonProperty("last_modified")
    private Date lastModified;

    private String hash;
    private Map<String, Serializable> metadata = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Serializable> getMetadata() {
        return metadata;
    }

    @JsonAnySetter
    public void setMetadata(String key, Serializable value) {
        metadata.put(key, value);
    }
}
