package it.smartcommunitylabdhub.core.models.converters.commands;

import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.converters.interfaces.ConverterCommand;

public class ConvertCommand<S, R> implements ConverterCommand<S, R> {
    private final Converter<S, R> converter;
    private final S input;

    public ConvertCommand(Converter<S, R> converter, S input) {
        this.converter = converter;
        this.input = input;
    }

    @Override
    public R execute() {
        return converter.convert(input);
    }
}