package it.smartcommunitylabdhub.commons.models.metrics;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonPropertyOrder(alphabetic = true)
public class Metrics implements Serializable {
	
	private String id;
	
	private String entityName;
	
	private String entityId;
	
	private String name;
    
	private  Number[] data;

}
