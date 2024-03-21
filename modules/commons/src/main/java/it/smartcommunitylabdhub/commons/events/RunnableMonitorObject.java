package it.smartcommunitylabdhub.commons.events;

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
public class RunnableMonitorObject implements Serializable {

    //TODO send run event to RunManager(todo) create an object of type RunState with stateId, runId, project, framework....
    private String runId;
    private String stateId;
    private String project;
    private String task;
    private String framework;
}
