package it.smartcommunitylabdhub.commons.utils;

import it.smartcommunitylabdhub.commons.accessors.fields.EmbeddedFieldAccessor;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.base.MetadataDTO;
import it.smartcommunitylabdhub.commons.models.base.SpecDTO;
import it.smartcommunitylabdhub.commons.models.base.StatusDTO;

public class EmbedUtils {

    public static <T extends BaseDTO & MetadataDTO> T embed(T dto) {
        EmbeddedFieldAccessor embeddedAccessor = EmbeddedFieldAccessor.with(dto.getMetadata());
        if (embeddedAccessor.isEmbedded()) {
            //embedded dto, use as is
            return dto;
        }
        //transform into embedded representation
        //no id
        dto.setId(null);
        //no specs
        if (dto instanceof SpecDTO) {
            ((SpecDTO) dto).setSpec(null);
        }

        if (dto instanceof StatusDTO) {
            ((StatusDTO) dto).setStatus(null);
        }
        //TODO evaluate removing metadata
        // dto.setMetadata(null);

        return dto;
    }

    private EmbedUtils() {}
}
