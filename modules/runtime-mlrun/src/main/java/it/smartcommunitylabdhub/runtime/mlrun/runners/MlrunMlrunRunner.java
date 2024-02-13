package it.smartcommunitylabdhub.runtime.mlrun.runners;

import it.smartcommunitylabdhub.commons.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.commons.models.accessors.fields.RunFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import it.smartcommunitylabdhub.runtime.mlrun.models.specs.run.RunMlrunSpec;

import java.util.*;

/**
 * MlrunMlrunRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "mlrun", task = "mlrun")
 */
public class MlrunMlrunRunner implements Runner {

    private static final String TASK = "mlrun";
    private final String image;
    private final Map<String, Set<String>> groupedSecrets;

    public MlrunMlrunRunner(String image, Map<String, Set<String>> groupedSecrets) {
        this.image = image;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {
        // Retrieve information about RunMlrunSpec
        RunMlrunSpec runMlrunSpec = RunMlrunSpec.builder().build();
        runMlrunSpec.configure(runDTO.getSpec());

        RunFieldAccessor runDefaultFieldAccessor = RunFieldAccessor.with(
                JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(runDTO, JacksonMapper.typeRef)
        );

        List<CoreEnv> coreEnvList = new ArrayList<>(
                List.of(new CoreEnv("PROJECT_NAME", runDTO.getProject()), new CoreEnv("RUN_ID", runDTO.getId()))
        );

        Optional.ofNullable(runMlrunSpec.getTaskSpec().getEnvs()).ifPresent(coreEnvList::addAll);

        //TODO: Create runnable using information from Run completed spec.
        K8sJobRunnable k8sJobRunnable = K8sJobRunnable
                .builder()
                .runtime(MlrunRuntime.RUNTIME)
                .task(TASK)
                .image(image)
                .command("python")
                .args(List.of("wrapper.py").toArray(String[]::new))
                .resources(runMlrunSpec.getTaskSpec().getResources())
                .nodeSelector(runMlrunSpec.getTaskSpec().getNodeSelector())
                .volumes(runMlrunSpec.getTaskSpec().getVolumes())
                .secrets(groupedSecrets)
                .envs(coreEnvList)
                .state(runDefaultFieldAccessor.getState())
                .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;
    }
}
