package it.smartcommunitylabdhub.framework.kaniko.runnables;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class ContextSource implements Serializable {

    private String name;

    private String base64;
}
