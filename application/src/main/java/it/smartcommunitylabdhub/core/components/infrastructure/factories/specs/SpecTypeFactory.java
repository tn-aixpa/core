package it.smartcommunitylabdhub.core.components.infrastructure.factories.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.core.CoreApplication;
import jakarta.annotation.PostConstruct;
import java.util.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

/**
 * The `SpecTypeFactory` class is responsible for scanning the classpath to discover
 * classes annotated with `@SpecType`. It extracts the information from these classes and
 * registers them in the `SpecRegistry` for later instantiation.
 */
@Component
public class SpecTypeFactory {

    private final SpecRegistry specRegistry;

    /**
     * Constructor to inject the `SpecRegistry` instance.
     *
     * @param specRegistry The `SpecRegistry` used to register discovered spec types.
     */
    public SpecTypeFactory(SpecRegistry specRegistry) {
        this.specRegistry = specRegistry;
    }

    /**
     * This method is annotated with `@PostConstruct`, ensuring it's executed after bean
     * instantiation. It scans the classpath to discover classes annotated with `@SpecType`,
     * extracts the relevant information, and registers them in the `SpecRegistry`.
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    public void scanForSpecTypes() {
        // Create a component scanner to find classes with SpecType annotations.
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(SpecType.class));

        // Detect the base packages based on ComponentScan annotation in CoreApplication.
        List<String> basePackages = getBasePackages();

        // Map to store discovered spec types and their corresponding classes.
        Map<String, Class<? extends Spec>> specTypes = new HashMap<>();

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);

            for (BeanDefinition beanDefinition : candidateComponents) {
                String className = beanDefinition.getBeanClassName();
                try {
                    // Load the class and check for SpecType annotation.
                    Class<? extends Spec> specClass = (Class<? extends Spec>) Class.forName(className);
                    SpecType specTypeAnnotation = specClass.getAnnotation(SpecType.class);
                    String specKey = specTypeAnnotation.kind() + "_" + specTypeAnnotation.entity().name().toLowerCase();
                    if (!specTypeAnnotation.runtime().isEmpty()) {
                        specKey = specTypeAnnotation.runtime() + "_" + specKey;
                    }
                    specTypes.put(specKey, specClass);
                } catch (ClassNotFoundException e) {
                    // Handle exceptions when a class is not found.
                }
            }
        }

        // Register the discovered spec types in the SpecRegistry for later instantiation.
        specRegistry.registerSpecTypes(specTypes);
    }

    /**
     * Automatically detects the base packages by inspecting the ComponentScan annotation
     * in the CoreApplication class.
     *
     * @return A list of base packages specified in the ComponentScan annotation.
     */
    private List<String> getBasePackages() {
        List<String> basePackages = new ArrayList<>();
        ComponentScan componentScan = CoreApplication.class.getAnnotation(ComponentScan.class);
        if (componentScan != null) {
            Collections.addAll(basePackages, componentScan.basePackages());
        }
        if (basePackages.isEmpty()) {
            throw new IllegalArgumentException("Base package not specified in @ComponentScan");
        }
        return basePackages;
    }
}
