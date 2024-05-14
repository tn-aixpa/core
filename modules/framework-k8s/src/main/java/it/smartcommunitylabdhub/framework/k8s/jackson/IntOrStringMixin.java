package it.smartcommunitylabdhub.framework.k8s.jackson;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = IntOrStringJacksonSerializer.class)
public class IntOrStringMixin {}
