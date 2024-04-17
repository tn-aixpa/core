package it.smartcommunitylabdhub.framework.k8s.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.List;
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
    @NotNull
    private VolumeType volumeType;

    @JsonProperty("mount_path")
    @NotBlank
    private String mountPath;

    @NotBlank
    private String name;

    private Map<String, String> spec;

    @JsonProperty("key_to_path")
    private List<CoreVolumeKeyToPath> keyToPath;

    public enum VolumeType {
        config_map,
        secret,
        persistent_volume_claim,
        empty_dir,
    }
}
