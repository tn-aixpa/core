package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.commons.annotations.common.ConverterType;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;

@ConverterType(type = "integer")
public class IntegerConverter implements Converter<String, Integer> {

    @Override
    public Integer convert(String input) throws CustomException {
        return Integer.parseInt(input);
    }

    @Override
    public String reverseConvert(Integer input) throws CustomException {
        return input.toString();
    }
}
