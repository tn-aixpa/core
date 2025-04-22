package it.smartcommunitylabdhub.commons.models.trigger;

import it.smartcommunitylabdhub.commons.infrastructure.TriggerRun;
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
public class TriggerExecutionEvent<J extends TriggerJob> {

    private TriggerRun<J> run;
    private TriggerEvent event;

    public J getJob() {
        return run != null ? run.getJob() : null;
    }

    public String getId() {
        return getJob() != null ? getJob().getId() : null;
    }
}
