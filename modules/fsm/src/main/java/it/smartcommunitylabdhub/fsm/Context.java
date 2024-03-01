package it.smartcommunitylabdhub.fsm;

import java.util.Optional;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Context<C> {

    private Optional<C> value;
}
