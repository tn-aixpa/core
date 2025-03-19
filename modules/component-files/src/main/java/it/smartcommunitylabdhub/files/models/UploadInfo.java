package it.smartcommunitylabdhub.files.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UploadInfo {

    private String path;
    private String url;
    private Instant expiration;
    private String uploadId;

    @Override
    public String toString() {
        return path + "_" + url + "_" + (expiration != null ? expiration.toEpochMilli() : "0");
    }
}
