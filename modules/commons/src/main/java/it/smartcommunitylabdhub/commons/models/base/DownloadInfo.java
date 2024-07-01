package it.smartcommunitylabdhub.commons.models.base;

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
public class DownloadInfo {

    private String path;
    private String url;
    private Instant expiration;

    @Override
    public String toString() {
        return path + "_" + url + "_" + (expiration != null ? expiration.toEpochMilli() : "0");
    }
}
