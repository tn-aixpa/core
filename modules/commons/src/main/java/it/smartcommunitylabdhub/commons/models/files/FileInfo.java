package it.smartcommunitylabdhub.commons.models.files;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FileInfo implements Serializable {

    private String path;

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
