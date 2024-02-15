package it.smartcommunitylabdhub.runtime.dbt.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RunDbtSpecFactory implements SpecFactory<RunDbtSpec> {

    @Override
    public RunDbtSpec create() {
        return new RunDbtSpec();
    }

    @Override
    public RunDbtSpec create(Map<String, Serializable> data) {
        return new RunDbtSpec(data);
    }
}
