package it.smartcommunitylabdhub.core.models.converters.commands;

import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.converters.interfaces.ConverterCommand;

public class ReverseConvertCommand<S, R> implements ConverterCommand<R, S> {
    private final Converter<S, R> converter;
    private final R input;

    public ReverseConvertCommand(Converter<S, R> converter, R input) {
        this.converter = converter;
        this.input = input;
    }

    @Override
    public S execute() {
        return converter.reverseConvert(input);
    }
}
