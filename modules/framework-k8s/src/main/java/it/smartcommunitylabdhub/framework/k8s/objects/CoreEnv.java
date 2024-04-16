package it.smartcommunitylabdhub.framework.k8s.objects;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record CoreEnv(@NotBlank String name, @NotBlank String value) implements Serializable {}
