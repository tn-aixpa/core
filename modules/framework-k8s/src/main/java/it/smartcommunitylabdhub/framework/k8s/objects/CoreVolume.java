package it.smartcommunitylabdhub.framework.k8s.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoreVolume implements Serializable {

    @JsonProperty("volume_type")
    private VolumeType volumeType;

    @JsonProperty("mount_path")
    private String mountPath;

    private String name;

    private Map<String, String> spec;

    public enum VolumeType {
        config_map,
        secret,
        persistent_volume_claim,
        empty_dir,
    }
}
