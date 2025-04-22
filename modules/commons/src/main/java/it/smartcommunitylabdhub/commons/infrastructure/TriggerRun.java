package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.models.trigger.TriggerJob;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class TriggerRun<J extends TriggerJob> {

    private J job;

    @Builder.Default
    private Instant timestamp = Instant.now();

    private Map<String, Serializable> details;

    public String getId() {
        return job != null ? job.getId() : null;
    }

    public String getUser() {
        return job != null ? job.getUser() : null;
    }
}
