package it.smartcommunitylabdhub.commons.utils;

import it.smartcommunitylabdhub.commons.accessors.fields.EmbeddedFieldAccessor;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;

public class EmbedUtils {

    public static <T extends BaseDTO> T embed(T dto) {
        EmbeddedFieldAccessor embeddedAccessor = EmbeddedFieldAccessor.with(dto.getMetadata());
        if (embeddedAccessor.isEmbedded()) {
            //embedded dto, use as is
            return dto;
        }
        //transform into embedded representation
        //no id
        dto.setId(null);
        //no specs
        dto.setSpec(null);
        dto.setStatus(null);
        //clear extra
        dto.setExtra(null);

        //TODO evaluate removing metadata
        // dto.setMetadata(null);

        return dto;
    }

    private EmbedUtils() {}
}
