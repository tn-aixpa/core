package it.smartcommunitylabdhub.framework.k8s.objects;

import java.io.Serializable;
import java.util.List;
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
public class CoreContainer implements Serializable {

    String name;
    String image;
    List<String> command;
    List<String> args;
    String workingDir;
    List<CoreEnv> envs;
}
