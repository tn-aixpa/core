package it.smartcommunitylabdhub.commons.events;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RunMonitorObject implements Serializable {

    //TODO send run event to RunManager(todo) create an object of type RunState with stateId, runId, project, framework....
    private String runId;
    private String stateId;
    private String project;
    private String task;
    private String framework;
}
