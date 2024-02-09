package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.commons.exceptions.CustomException;

public abstract class AbstractConverter {

    public abstract <C> C reverseByClass(byte[] cborBytes, Class<C> targetClass) throws CustomException;
}
