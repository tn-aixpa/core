package it.smartcommunitylabdhub.core.components.label;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.commons.models.base.BaseMetadata;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import it.smartcommunitylabdhub.core.models.entities.label.LabelEntity;
import it.smartcommunitylabdhub.core.models.events.EntityEvent;
import it.smartcommunitylabdhub.core.repositories.LabelRepository;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LabelComponent {
	@Autowired
	LabelRepository labelRepository;
	
	@Autowired
    private AttributeConverter<Map<String, Serializable>, byte[]> converter;	
	
    private void updateLabels(String project, Set<String> labels) {
    	if(labels != null) {
    		labels.forEach(label -> {
    			try {
        			LabelEntity entity = labelRepository.findByProjectAndLabelIgnoreCase(project, label.trim());
        			if(entity == null) {
        				entity = LabelEntity.builder()
        						.id(UUID.randomUUID().toString())
        						.project(project)
        						.label(label.toLowerCase().trim())
        						.build();
        				labelRepository.save(entity);
        				log.debug("updateLabels[{}]:{}", project, label);
        			}					
				} catch (Exception e) {
					LabelComponent.log.warn("updateLabels[{}][{}]:{}", project, label, e.getMessage());
				}
    		});
    	}	
	}
    
    @Async
    @EventListener
    public void receive(EntityEvent<? extends BaseEntity> event) {
        if (event.getEntity() == null || event.getEntity().getMetadata() == null) {
            return;
        }
        Map<String, Serializable> map = converter.convertToEntityAttribute(event.getEntity().getMetadata());
        BaseMetadata metadata = new BaseMetadata();
        metadata.configure(map);
        updateLabels(event.getEntity().getProject(), metadata.getLabels());
    }
    
}
