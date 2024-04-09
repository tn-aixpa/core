package it.smartcommunitylabdhub.commons.models.entities.label;

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
public class Label {

    private String id;

    private String project;

    private String label;
}
