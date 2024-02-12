package it.smartcommunitylabdhub.framework.k8s.objects;

import java.io.Serializable;

public record CoreEnv(String name, String value) implements Serializable {}
