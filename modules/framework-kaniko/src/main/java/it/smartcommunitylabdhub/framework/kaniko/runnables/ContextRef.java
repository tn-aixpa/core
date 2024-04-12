package it.smartcommunitylabdhub.framework.kaniko.runnables;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class ContextRef implements Serializable {
    private String destination;
    private String protocol;
    private String source;
}
