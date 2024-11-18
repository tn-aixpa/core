package it.smartcommunitylabdhub.core.models.specs.run;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.run.RunBaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "run", entity = EntityName.RUN)
public class RunRunSpec extends RunBaseSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
