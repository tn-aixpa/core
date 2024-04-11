package it.smartcommunitylabdhub.framework.k8s.objects;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoreResource implements Serializable {

    @Nullable
    private CoreResourceDefinition cpu;

    @Nullable
    private CoreResourceDefinition mem;

    @Nullable
    private CoreResourceDefinition gpu;

    public record CoreResourceDefinition(
        @Pattern(regexp = "[\\d]+|^([0-9])+([a-zA-Z])+$") String requests,
        @Pattern(regexp = "[\\d]+|^([0-9])+([a-zA-Z])+$") String limits
    )
        implements Serializable {}
}
