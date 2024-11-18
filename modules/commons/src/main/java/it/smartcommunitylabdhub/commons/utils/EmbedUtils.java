package it.smartcommunitylabdhub.commons.utils;

import it.smartcommunitylabdhub.commons.accessors.fields.EmbeddedFieldAccessor;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.metadata.BaseMetadata;
import it.smartcommunitylabdhub.commons.models.metadata.MetadataDTO;
import it.smartcommunitylabdhub.commons.models.specs.SpecDTO;
import it.smartcommunitylabdhub.commons.models.status.StatusDTO;

public class EmbedUtils {

    public static <T extends BaseDTO & MetadataDTO> T embed(T dto) {
        return embed(dto, true);
    }

    public static <T extends BaseDTO & MetadataDTO> T embed(T dto, boolean keepIds) {
        EmbeddedFieldAccessor embeddedAccessor = EmbeddedFieldAccessor.with(dto.getMetadata());
        if (embeddedAccessor.isEmbedded()) {
            //embedded dto, use as is
            return dto;
        }
        //transform into embedded representation
        if (!keepIds) {
            //no id
            dto.setId(null);
        }
        //no specs
        if (dto instanceof SpecDTO) {
            ((SpecDTO) dto).setSpec(null);
        }

        if (dto instanceof StatusDTO) {
            ((StatusDTO) dto).setStatus(null);
        }

        //keep only base metadata
        //TODO evaluate removing *all* metadata
        if (dto.getMetadata() != null) {
            dto.setMetadata(BaseMetadata.from(dto.getMetadata()).toMap());
        }

        return dto;
    }

    private EmbedUtils() {}
}
