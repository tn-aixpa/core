package it.smartcommunitylabdhub.framework.k8s.objects;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record CoreLabel(@NotBlank String name, @NotBlank String value) implements Serializable {}
