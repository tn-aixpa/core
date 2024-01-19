package it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors;

import it.smartcommunitylabdhub.core.CoreApplication;
import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * The `SpecTypeFactory` class is responsible for scanning the classpath to discover
 * classes annotated with `@SpecType`. It extracts the information from these classes and
 * registers them in the `SpecRegistry` for later instantiation.
 */
@Component
public class AccessorTypeFactory {
    private final AccessorRegistry<?> accessorRegistry;

    /**
     * Constructor to inject the `AccessorRegistry` instance.
     *
     * @param accessorRegistry The `AccessorRegistry` used to register discovered accessor types.
     */
    public AccessorTypeFactory(AccessorRegistry<?> accessorRegistry) {
        this.accessorRegistry = accessorRegistry;
    }

    /**
     * This method is annotated with `@PostConstruct`, ensuring it's executed after bean
     * instantiation. It scans the classpath to discover classes annotated with `@AccessorType`,
     * extracts the relevant information, and registers them in the `AccessorRegistry`.
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    public void scanForAccessorTypes() {
        // Create a component scanner to find classes with AccessorType annotations.
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(AccessorType.class));

        // Detect the base packages based on ComponentScan annotation in CoreApplication.
        List<String> basePackages = getBasePackages();

        // Map to store discovered accessor types and their corresponding classes.
        Map<String, Class<? extends Accessor<Object>>> accessorTypes = new HashMap<>();

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);

            for (BeanDefinition beanDefinition : candidateComponents) {
                String className = beanDefinition.getBeanClassName();
                try {
                    // Load the class and check for AccessorType annotation.
                    Class<? extends Accessor<Object>> accessorClass = (Class<? extends Accessor<Object>>) Class.forName(className);
                    AccessorType accessorTypeAnnotation = accessorClass.getAnnotation(AccessorType.class);
                    String accessorKey = accessorTypeAnnotation.kind() + "_" + accessorTypeAnnotation.entity().name().toLowerCase();
                    accessorTypes.put(accessorKey, accessorClass);
                } catch (ClassNotFoundException e) {
                    // Handle exceptions when a class is not found.
                }
            }
        }

        // Register the discovered accessor types in the AccessorRegistry for later instantiation.
        accessorRegistry.registerAccessorTypes(accessorTypes);
    }

    /**
     * Automatically detects the base packages by accessorizing the ComponentScan annotation
     * in the CoreApplication class.
     *
     * @return A list of base packages accessorized in the ComponentScan annotation.
     */
    private List<String> getBasePackages() {
        List<String> basePackages = new ArrayList<>();
        ComponentScan componentScan = CoreApplication.class.getAnnotation(ComponentScan.class);
        if (componentScan != null) {
            Collections.addAll(basePackages, componentScan.basePackages());
        }
        if (basePackages.isEmpty()) {
            throw new IllegalArgumentException("Base package not accessorified in @ComponentScan");
        }
        return basePackages;
    }
}
