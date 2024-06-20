package it.smartcommunitylabdhub.runtime.python.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.python.specs.function.PythonFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.specs.run.PythonRunSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonServeTaskSpec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PythonServeBuilder implements Builder<PythonFunctionSpec, PythonServeTaskSpec, PythonRunSpec> {

    @Override
    public PythonRunSpec build(PythonFunctionSpec funSpec, PythonServeTaskSpec taskSpec, PythonRunSpec runSpec) {
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        map.putAll(funSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        PythonRunSpec pythonSpec = new PythonRunSpec(map);
        pythonSpec.setFunctionSpec(funSpec);
        return pythonSpec;
    }
}
