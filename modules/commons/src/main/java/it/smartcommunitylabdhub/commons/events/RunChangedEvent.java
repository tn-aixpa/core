package it.smartcommunitylabdhub.commons.events;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunChangedEvent {
    private RunMonitorObject runMonitorObject;
}
