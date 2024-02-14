package it.smartcommunitylabdhub.framework.k8s.objects;

import java.io.Serializable;

public record CoreLabel(String name, String value) implements Serializable {}
