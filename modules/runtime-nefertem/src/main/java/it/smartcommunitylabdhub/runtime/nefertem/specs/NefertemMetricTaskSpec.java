package it.smartcommunitylabdhub.runtime.nefertem.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.runtime.nefertem.NefertemRuntime;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = NefertemRuntime.RUNTIME, kind = NefertemMetricTaskSpec.KIND, entity = EntityName.TASK)
public class NefertemMetricTaskSpec extends NefertemBaseTaskSpec {

    public static final String KIND = NefertemRuntime.RUNTIME + "+metric";

    public NefertemMetricTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
