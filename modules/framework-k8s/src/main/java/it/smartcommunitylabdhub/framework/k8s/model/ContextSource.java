package it.smartcommunitylabdhub.framework.k8s.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContextSource implements Serializable {

    private String name;

    private String base64;
}
