package it.smartcommunitylabdhub.core.models.builders.label;

import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.commons.models.entities.label.Label;
import it.smartcommunitylabdhub.core.models.entities.label.LabelEntity;

@Component
public class LabelDTOBuilder {

    public Label build(LabelEntity entity) {
        return Label
            .builder()
            .id(entity.getId())
            .project(entity.getProject())
            .label(entity.getLabel())
            .build();
    }

}
