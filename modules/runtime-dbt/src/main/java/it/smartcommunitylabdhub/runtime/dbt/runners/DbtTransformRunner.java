package it.smartcommunitylabdhub.runtime.dbt.runners;

import it.smartcommunitylabdhub.commons.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces.RunFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.run.RunDbtSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DbtTransformRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "transform")
 */
public class DbtTransformRunner implements Runner {

  private static final String TASK = "transform";

  private final String image;
  private final Map<String, Set<String>> groupedSecrets;

  public DbtTransformRunner(
    String image,
    Map<String, Set<String>> groupedSecrets
  ) {
    this.image = image;
    this.groupedSecrets = groupedSecrets;
  }

  @Override
  public K8sJobRunnable produce(Run runDTO) {
    // Retrieve information about RunDbtSpec
    RunDbtSpec runDbtSpec = RunDbtSpec.builder().build();
    runDbtSpec.configure(runDTO.getSpec());

    List<CoreEnv> coreEnvList = new ArrayList<>(
      List.of(
        new CoreEnv("PROJECT_NAME", runDTO.getProject()),
        new CoreEnv("RUN_ID", runDTO.getId())
      )
    );
    if (runDbtSpec.getTaskSpec().getEnvs() != null) coreEnvList.addAll(
      runDbtSpec.getTaskSpec().getEnvs()
    );

    coreEnvList.addAll(runDbtSpec.getTaskSpec().getEnvs());
    HashMap<String, Object> map =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        runDTO,
        JacksonMapper.typeRef
      );
    RunFieldAccessor runDefaultFieldAccessor = RunFieldAccessor.with(map);

    //TODO: Create runnable using information from Run completed spec.
    K8sJobRunnable k8sJobRunnable = K8sJobRunnable
      .builder()
      .runtime(DbtRuntime.RUNTIME)
      .task(TASK)
      .image(image)
      .command("python")
      .args(List.of("wrapper.py").toArray(String[]::new))
      .resources(runDbtSpec.getTaskSpec().getResources())
      .nodeSelector(runDbtSpec.getTaskSpec().getNodeSelector())
      .volumes(runDbtSpec.getTaskSpec().getVolumes())
      .secrets(groupedSecrets)
      .envs(coreEnvList)
      .state(runDefaultFieldAccessor.getState())
      .build();

    k8sJobRunnable.setId(runDTO.getId());
    k8sJobRunnable.setProject(runDTO.getProject());

    return k8sJobRunnable;
  }
}
