package it.smartcommunitylabdhub.core.models.converters;

import it.smartcommunitylabdhub.commons.models.enums.State;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StateStringAttributeConverter implements AttributeConverter<State, String> {

    @Override
    public String convertToDatabaseColumn(State attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.name();
    }

    @Override
    public State convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        //enum is uppercase
        return State.valueOf(dbData.toUpperCase());
    }
}
