package it.smartcommunitylabdhub.framework.kaniko.runnables;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContextRef implements Serializable {

    private String destination;
    private String protocol;
    private String source;
}
