package it.smartcommunitylabdhub.core.components.validators;

import it.smartcommunitylabdhub.commons.annotations.validators.ValidatorComponent;
import it.smartcommunitylabdhub.core.models.validators.interfaces.BaseValidator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidatorFactory {

    private final Map<String, ? extends BaseValidator> validatorMap;

    /**
     * Constructor to create the ValidatorFactory with a list of Validators.
     *
     * @param builders The list of Validators to be managed by the factory.
     */
    public ValidatorFactory(List<? extends BaseValidator> builders) {
        validatorMap =
            builders.stream().collect(Collectors.toMap(this::getValidatorFromAnnotation, Function.identity()));
    }

    /**
     * Get the Validator string from the @ValidatorComponent annotation for a given Validator.
     *
     * @param validator The Validator for which to extract the Validator string.
     * @return The Validator string extracted from the @ValidatorComponent annotation.
     * @throws IllegalArgumentException If no @ValidatorComponent annotation is found for the
     *                                  builder.
     */
    private <R extends BaseValidator> String getValidatorFromAnnotation(R validator) {
        Class<?> runnerClass = validator.getClass();
        if (runnerClass.isAnnotationPresent(ValidatorComponent.class)) {
            ValidatorComponent annotation = runnerClass.getAnnotation(ValidatorComponent.class);
            return annotation.runtime() + "+" + annotation.task();
        }
        throw new IllegalArgumentException(
            "No @ValidatorComponent annotation found for runner: " + runnerClass.getName()
        );
    }

    /**
     * Get the Validator for the given Validator.
     *
     * @param runtime The Validator string representing the specific Validator for which to
     *                retrieve the Validator.
     * @param task    The task string representing the specific task action for a given Validator.
     * @param <R>     Is a runnable that exteds Runnable
     * @return The Validator for the specified Validator.
     * @throws IllegalArgumentException If no Validator is found for the given Validator.
     */
    public <R extends BaseValidator> R getValidator(String runtime, String task) {
        @SuppressWarnings("unchecked")
        R builder = (R) validatorMap.get(runtime + "+" + task);
        if (builder == null) {
            throw new IllegalArgumentException("No builder found for name: " + runtime + "+" + task);
        }
        return builder;
    }
}
