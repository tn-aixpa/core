package it.smartcommunitylabdhub.modules.nefertem.components.runtimes;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.components.infrastructure.runtimes.BaseRuntime;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.factories.RunDefaultFieldAccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.base.RunStatus;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.factories.RunRunSpecFactory;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.nefertem.components.builders.NefertemInferBuilder;
import it.smartcommunitylabdhub.modules.nefertem.components.builders.NefertemMetricBuilder;
import it.smartcommunitylabdhub.modules.nefertem.components.builders.NefertemProfileBuilder;
import it.smartcommunitylabdhub.modules.nefertem.components.builders.NefertemValidateBuilder;
import it.smartcommunitylabdhub.modules.nefertem.components.runners.NefertemInferRunner;
import it.smartcommunitylabdhub.modules.nefertem.components.runners.NefertemMetricRunner;
import it.smartcommunitylabdhub.modules.nefertem.components.runners.NefertemProfileRunner;
import it.smartcommunitylabdhub.modules.nefertem.components.runners.NefertemValidateRunner;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.factories.FunctionNefertemSpecFactory;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@RuntimeComponent(runtime = NefertemRuntime.RUNTIME)
public class NefertemRuntime extends BaseRuntime<FunctionNefertemSpec, RunNefertemSpec<? extends K8sTaskBaseSpec>, K8sJobRunnable> {


    public static final String RUNTIME = "nefertem";

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    FunctionNefertemSpecFactory functionNefertemSpecFactory;

    @Autowired
    RunDefaultFieldAccessorFactory runDefaultFieldAccessorFactory;

    @Autowired
    RunRunSpecFactory runRunSpecFactory;


    @Value("${runtime.nefertem.image}")
    private String image;

    public NefertemRuntime(BuilderFactory builderFactory,
                           RunnerFactory runnerFactory) {
        super(builderFactory, runnerFactory);
    }

    @Override
    public RunNefertemSpec<? extends K8sTaskBaseSpec> build(
            FunctionNefertemSpec funSpec,
            TaskBaseSpec taskSpec,
            RunBaseSpec runSpec,
            String kind) {

        /**
         *  As an alternative, you can use the code below to retrieve the correct builder.
         *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
         *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
         *  the builder in this way remember also that you have to register the builder as
         *  a component using the follow annotation: `@BuilderComponent(runtime = "dbt", task = "transform")`
         *  Only by doing this you can get the bean related
         * <p>
         *      NefertemInferBuilder builder = (NefertemInferBuilder) getBuilder(kind);
         */

        // NefertemInferBuilder b = getBuilder(kind);


        // Retrieve builder using task kind
        switch (kind) {
            case "infer" -> {

                NefertemInferBuilder builder = new NefertemInferBuilder();

                return builder.build(
                        funSpec,
                        specRegistry.createSpec(
                                "infer",
                                EntityName.TASK,
                                taskSpec.toMap()
                        ),
                        specRegistry.createSpec(
                                "run",
                                EntityName.RUN,
                                runSpec.toMap()
                        ));

            }
            case "validate" -> {

                NefertemValidateBuilder builder = new NefertemValidateBuilder();

                return builder.build(
                        funSpec,
                        specRegistry.createSpec(
                                "validate",
                                EntityName.TASK,
                                taskSpec.toMap()
                        ),
                        specRegistry.createSpec(
                                "run",
                                EntityName.RUN,
                                runSpec.toMap()
                        ));

            }
            case "metric" -> {

                NefertemMetricBuilder builder = new NefertemMetricBuilder();

                return builder.build(
                        funSpec,
                        specRegistry.createSpec(
                                "metric",
                                EntityName.TASK,
                                taskSpec.toMap()
                        ),
                        specRegistry.createSpec(
                                "run",
                                EntityName.RUN,
                                runSpec.toMap()
                        ));

            }
            case "profile" -> {

                NefertemProfileBuilder builder = new NefertemProfileBuilder();

                return builder.build(
                        funSpec,
                        specRegistry.createSpec(
                                "profile",
                                EntityName.TASK,
                                taskSpec.toMap()
                        ),
                        specRegistry.createSpec(
                                "run",
                                EntityName.RUN,
                                runSpec.toMap()
                        ));

            }
            default -> throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @Override
    public K8sJobRunnable run(Run runDTO) {

        /**
         *  As an alternative, you can use the code below to retrieve the correct runner.
         *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
         *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
         *  the Runner using specRegistry remember that you have to register also each Runner
         *  component in this way : `@RunnerComponent(runtime = "dbt", task = "transform")`
         *  Only by doing this you can get the bean related
         * <p>
         *      // Retrieve base run spec to use task
         *      RunRunSpec runBaseSpec = specRegistry.createSpec(
         *              runDTO.getKind(),
         *              SpecEntity.RUN,
         *              runDTO.getSpec()
         *      );
         *      RunAccessor runAccessor = RunUtils.parseRun(runBaseSpec.getTask());
         *      Runner runner = getRunner(runAccessor.getTask());
         */


        // Crete spec for run
        RunRunSpec runRunSpec = runRunSpecFactory.create();
        runRunSpec.configure(runDTO.getSpec());

        // Create string run accessor from task
        RunAccessor runAccessor = RunUtils.parseRun(runRunSpec.getTask());

        // Create and configure function nefertem spec
        FunctionNefertemSpec functionNefertemSpec = functionNefertemSpecFactory.create();
        functionNefertemSpec.configure(runDTO.getSpec());
        

        // Create and configure default run field accessor
        RunDefaultFieldAccessor runDefaultFieldAccessor = runDefaultFieldAccessorFactory.create();
        runDefaultFieldAccessor.configure(
                JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                        runDTO,
                        JacksonMapper.typeRef)
        );

        return switch (runAccessor.getTask()) {
            case "infer" -> new NefertemInferRunner(
                    image,
                    runDefaultFieldAccessor).produce(runDTO);
            case "validate" -> new NefertemValidateRunner(
                    image,
                    runDefaultFieldAccessor).produce(runDTO);
            case "profile" -> new NefertemProfileRunner(
                    image,
                    runDefaultFieldAccessor).produce(runDTO);
            case "metric" -> new NefertemMetricRunner(
                    image,
                    runDefaultFieldAccessor).produce(runDTO);
            default -> throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Kind not recognized. Cannot retrieve the right Runner",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        };

    }


    @Override
    public RunStatus parse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parse'");
    }

}
