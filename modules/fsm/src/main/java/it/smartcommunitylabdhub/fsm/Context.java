package it.smartcommunitylabdhub.fsm;

import jakarta.annotation.Nullable;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Context<C> {

    @Nullable
    private C value;
}
