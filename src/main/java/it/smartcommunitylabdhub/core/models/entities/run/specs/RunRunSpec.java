package it.smartcommunitylabdhub.core.models.entities.run.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "run", entity = EntityName.RUN)
public class RunRunSpec extends RunBaseSpec<RunRunSpec> {

    @Override
    protected void configureSpec(RunRunSpec runRunSpec) {
        super.configureSpec(runRunSpec);

    }
}

