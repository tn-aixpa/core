package it.smartcommunitylabdhub.modules.nefertem.components.runtimes;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.runtimes.BaseRuntime;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.base.RunStatus;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.modules.nefertem.components.builders.NefertemInferBuilder;
import it.smartcommunitylabdhub.modules.nefertem.components.builders.NefertemMetricBuilder;
import it.smartcommunitylabdhub.modules.nefertem.components.builders.NefertemProfileBuilder;
import it.smartcommunitylabdhub.modules.nefertem.components.builders.NefertemValidateBuilder;
import it.smartcommunitylabdhub.modules.nefertem.components.runners.NefertemInferRunner;
import it.smartcommunitylabdhub.modules.nefertem.components.runners.NefertemMetricRunner;
import it.smartcommunitylabdhub.modules.nefertem.components.runners.NefertemProfileRunner;
import it.smartcommunitylabdhub.modules.nefertem.components.runners.NefertemValidateRunner;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@RuntimeComponent(runtime = "nefertem")
public class NefertemRuntime extends BaseRuntime<FunctionNefertemSpec> {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;


    @Value("${runtime.nefertem.image}")
    private String image;

    public NefertemRuntime(BuilderFactory builderFactory,
                           RunnerFactory runnerFactory) {
        super(builderFactory, runnerFactory);
    }

    @Override
    public RunBaseSpec<?> build(
            FunctionNefertemSpec funSpec,
            TaskBaseSpec<?> taskSpec,
            RunBaseSpec<?> runSpec,
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
                                SpecEntity.TASK,
                                taskSpec.toMap()
                        ),
                        specRegistry.createSpec(
                                "run",
                                SpecEntity.RUN,
                                runSpec.toMap()
                        ));

            }
            case "validate" -> {

                NefertemValidateBuilder builder = new NefertemValidateBuilder();

                return builder.build(
                        funSpec,
                        specRegistry.createSpec(
                                "validate",
                                SpecEntity.TASK,
                                taskSpec.toMap()
                        ),
                        specRegistry.createSpec(
                                "run",
                                SpecEntity.RUN,
                                runSpec.toMap()
                        ));

            }
            case "metric" -> {

                NefertemMetricBuilder builder = new NefertemMetricBuilder();

                return builder.build(
                        funSpec,
                        specRegistry.createSpec(
                                "metric",
                                SpecEntity.TASK,
                                taskSpec.toMap()
                        ),
                        specRegistry.createSpec(
                                "run",
                                SpecEntity.RUN,
                                runSpec.toMap()
                        ));

            }
            case "profile" -> {

                NefertemProfileBuilder builder = new NefertemProfileBuilder();

                return builder.build(
                        funSpec,
                        specRegistry.createSpec(
                                "profile",
                                SpecEntity.TASK,
                                taskSpec.toMap()
                        ),
                        specRegistry.createSpec(
                                "run",
                                SpecEntity.RUN,
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
    public Runnable run(Run runDTO) {

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


        RunRunSpec runBaseSpec = specRegistry.createSpec(
                runDTO.getKind(),
                SpecEntity.RUN,
                runDTO.getSpec()
        );
        RunAccessor runAccessor = RunUtils.parseRun(runBaseSpec.getTask());

        return switch (runAccessor.getTask()) {
            case "infer" -> new NefertemInferRunner(image).produce(runDTO);
            case "validate" -> new NefertemValidateRunner(image).produce(runDTO);
            case "profile" -> new NefertemProfileRunner(image).produce(runDTO);
            case "metric" -> new NefertemMetricRunner(image).produce(runDTO);
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
