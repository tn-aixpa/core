package it.smartcommunitylabdhub.core.models.converters.interfaces;

public interface ConverterFactory {
    <I, O> Converter<I, O> getConverter(String converter);
}
