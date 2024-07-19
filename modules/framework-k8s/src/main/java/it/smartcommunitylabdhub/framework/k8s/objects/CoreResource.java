package it.smartcommunitylabdhub.framework.k8s.objects;

import jakarta.annotation.Nullable;
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
}
