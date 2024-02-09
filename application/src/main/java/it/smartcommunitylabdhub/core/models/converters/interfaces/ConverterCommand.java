package it.smartcommunitylabdhub.core.models.converters.interfaces;

/**
 * The command factory is passed down because there will be the case that we
 * have nested object to convert:
 * 
 * Project -> List<Function>
 * 
 * in that case we ha to pass down the object because we need it in the
 * ConversionUtils.
 */
public interface ConverterCommand<T, R> {
    R execute();
}