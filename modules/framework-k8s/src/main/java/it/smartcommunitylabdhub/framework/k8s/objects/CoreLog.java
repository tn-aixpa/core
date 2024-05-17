package it.smartcommunitylabdhub.framework.k8s.objects;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record CoreLog(@NotBlank String pod, @NotBlank String value, String container, String namespace)
    implements Serializable {}
