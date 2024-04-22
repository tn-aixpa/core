package it.smartcommunitylabdhub.framework.kaniko.runnables;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContextSource implements Serializable {

    private String name;

    private String base64;
}
