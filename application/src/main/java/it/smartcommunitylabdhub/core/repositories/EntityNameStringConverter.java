package it.smartcommunitylabdhub.core.repositories;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.commons.models.entities.EntityName;

@Component
public class EntityNameStringConverter implements Converter<String, EntityName> {

    @Override
    @Nullable
    public EntityName convert(@NonNull String source) {
        //enum is uppercase
        String value = source.toUpperCase();

        //also handle pluralized values
        if (value.endsWith("S")) {
            value = value.substring(0, value.length() - 1);
        }

        return EntityName.valueOf(value);
    }
}
