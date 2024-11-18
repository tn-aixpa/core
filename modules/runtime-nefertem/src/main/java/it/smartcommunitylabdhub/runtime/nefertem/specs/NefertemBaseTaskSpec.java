package it.smartcommunitylabdhub.runtime.nefertem.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.framework.k8s.base.K8sFunctionTaskBaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NefertemBaseTaskSpec extends K8sFunctionTaskBaseSpec {

    protected String framework;

    @JsonProperty("exec_args")
    protected Map<String, Serializable> execArgs;

    protected Boolean parallel;

    @JsonProperty("num_worker")
    protected Integer numWorker;

    public NefertemBaseTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        NefertemBaseTaskSpec spec = mapper.convertValue(data, NefertemBaseTaskSpec.class);

        this.framework = spec.getFramework();
        this.execArgs = spec.getExecArgs();
        this.parallel = spec.getParallel();
        this.numWorker = spec.getNumWorker();
    }
}
