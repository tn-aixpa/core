package it.smartcommunitylabdhub.core.components.infrastructure.factories.converters;

import it.smartcommunitylabdhub.commons.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.converters.interfaces.ConverterFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConverterTypeFactory implements ConverterFactory {

    private final Map<String, Converter<?, ?>> converterMap;

    /**
     * Constructor to create the ConverterFactory with a list of Converters.
     *
     * @param converters The list of Converters to be managed by the factory.
     */
    public ConverterTypeFactory(List<Converter<?, ?>> converters) {
        converterMap =
            converters.stream().collect(Collectors.toMap(this::getConverterFromAnnotation, Function.identity()));
    }

    /**
     * Get the platform string from the @ConverterComponent annotation for a given Converter.
     *
     * @param converter The Converter for which to extract the platform string.
     * @return The platform string extracted from the @ConverterComponent annotation.
     * @throws IllegalArgumentException If no @ConverterComponent annotation is found for the
     *         converter.
     */
    private String getConverterFromAnnotation(Converter<?, ?> converter) {
        Class<?> converterClass = converter.getClass();
        if (converterClass.isAnnotationPresent(ConverterType.class)) {
            ConverterType annotation = converterClass.getAnnotation(ConverterType.class);
            return annotation.type();
        }
        throw new IllegalArgumentException(
            "No @ConverterComponent annotation found for converter: " + converterClass.getName()
        );
    }

    /**
     * Get the Converter for the given platform.
     *
     * @param converter The converter platform
     * @return The Converter for the specified platform.
     * @throws IllegalArgumentException If no Converter is found for the given platform.
     */
    @SuppressWarnings("unchecked")
    public <I, O> Converter<I, O> getConverter(String converter) {
        Converter<?, ?> concreteConverter = converterMap.get(converter);
        if (concreteConverter == null) {
            throw new IllegalArgumentException("No converter found for name: " + converter);
        }
        return (Converter<I, O>) concreteConverter;
    }
}
