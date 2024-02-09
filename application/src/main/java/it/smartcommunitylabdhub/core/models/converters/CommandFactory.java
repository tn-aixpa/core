package it.smartcommunitylabdhub.core.models.converters;

import it.smartcommunitylabdhub.core.models.converters.commands.ConvertCommand;
import it.smartcommunitylabdhub.core.models.converters.commands.ReverseConvertCommand;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.converters.interfaces.ConverterCommand;
import it.smartcommunitylabdhub.core.models.converters.interfaces.ConverterFactory;

public class CommandFactory {
    private final ConverterFactory converterFactory;

    public CommandFactory(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public <T, R> ConverterCommand<T, R> createConvertCommand(String kind, T input) {
        Converter<T, R> converter = getConverter(kind);
        return new ConvertCommand<>(converter, input);
    }

    public <T, R> ConverterCommand<R, T> createReverseConvertCommand(String kind, R input) {
        Converter<T, R> converter = getConverter(kind);
        return new ReverseConvertCommand<>(converter, input);
    }

    @SuppressWarnings("unchecked")
    private <T, R> Converter<T, R> getConverter(String kind) {
        return (Converter<T, R>) converterFactory.getConverter(kind);
    }
}
