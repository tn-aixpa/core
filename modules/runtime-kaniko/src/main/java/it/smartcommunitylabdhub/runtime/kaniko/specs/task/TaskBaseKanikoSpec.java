package it.smartcommunitylabdhub.runtime.kaniko.specs.task;

import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.runtime.kaniko.docker.DockerfileInstruction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TaskBaseKanikoSpec extends TaskBaseSpec {

    private Map<DockerfileInstruction, List<String>> instructions = new HashMap<>();
    private List<String> directives = new ArrayList<>();

    private String user;

    private String workdir;

    private String healthCheck;


    @Override
    public void configure(Map<String, Serializable> data) {
        TaskBaseKanikoSpec concreteSpec = mapper.convertValue(data, TaskBaseKanikoSpec.class);

        this.instructions = concreteSpec.getInstructions();
        this.directives = concreteSpec.getDirectives();
        this.user = concreteSpec.getUser();
        this.workdir = concreteSpec.getWorkdir();
        this.healthCheck = concreteSpec.getHealthCheck();
    }
}
