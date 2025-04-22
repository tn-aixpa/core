package it.smartcommunitylabdhub.trigger.scheduler.models;

import it.smartcommunitylabdhub.commons.models.trigger.TriggerJob;
import it.smartcommunitylabdhub.trigger.scheduler.SchedulerActuator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledTriggerJob implements TriggerJob {

    private String id;
    private String user;
    private String task;
    private String project;

    private String state;
    private String message;
    private String error;

    private String schedule;

    @Override
    public String getActuator() {
        return SchedulerActuator.ACTUATOR;
    }
}
