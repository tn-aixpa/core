package it.smartcommunitylabdhub.core.models.converters.interfaces;

import it.smartcommunitylabdhub.commons.exceptions.CustomException;

//TODO extend spring converter class to expose converters to spring
// public interface Converter<T, R> extends org.springframework.core.convert.converter.Converter<T, R> {
public interface Converter<T, R> {
    R convert(T input) throws CustomException;

    T reverseConvert(R input) throws CustomException;
}
