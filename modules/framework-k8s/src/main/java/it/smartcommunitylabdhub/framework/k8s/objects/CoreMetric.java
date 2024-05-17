package it.smartcommunitylabdhub.framework.k8s.objects;

import io.kubernetes.client.custom.ContainerMetrics;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

public record CoreMetric(
    @NotBlank String pod,
    @NotBlank List<ContainerMetrics> metrics,
    String timestamp,
    String window,
    String namespace
)
    implements Serializable {}
