package it.smartcommunitylabdhub.core.models.converters;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.core.models.converters.interfaces.ConverterCommand;

@Component
public class ConversionUtils {

    private static CommandFactory commandFactory;

    @Autowired
    public void setCommandFactory(CommandFactory commandFactory) {
        ConversionUtils.commandFactory = commandFactory;
    }

    public static <S, T> T convert(S source, String kind) {
        ConverterCommand<S, T> command = commandFactory.createConvertCommand(kind, source);
        return command.execute();
    }

    public static <S, T> T reverse(S source, String kind) {
        ConverterCommand<S, T> command = commandFactory.createReverseConvertCommand(kind, source);
        return command.execute();
    }

    public static <S, T> Collection<T> convertIterable(Collection<S> source, String kind,
            Class<T> returnType) {
        return source.stream().map(element -> {
            ConverterCommand<S, T> command = commandFactory.createConvertCommand(kind, element);
            return command.execute();
        }).collect(Collectors.toList());
    }

    public static <S, T> Collection<T> reverseIterable(Collection<S> source,
            String kind,
            Class<T> returnType) {
        return source.stream().map(element -> {
            ConverterCommand<S, T> command = commandFactory.createReverseConvertCommand(kind, element);
            return command.execute();
        }).collect(Collectors.toList());
    }

    // ** return command factory if needed */
    public static CommandFactory getCommandFactory() {
        return ConversionUtils.commandFactory;
    }

}
