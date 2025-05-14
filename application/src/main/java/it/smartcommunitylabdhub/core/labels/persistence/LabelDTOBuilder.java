package it.smartcommunitylabdhub.core.labels.persistence;

import it.smartcommunitylabdhub.commons.models.label.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelDTOBuilder {

    public Label build(LabelEntity entity) {
        return Label.builder().id(entity.getId()).project(entity.getProject()).label(entity.getLabel()).build();
    }
}
