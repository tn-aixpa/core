package it.smartcommunitylabdhub.commons.models.metrics;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonSerialize(using = NumberOrNumberArrayJacksonSerializer.class)
@JsonDeserialize(using = NumberOrNumberArrayJacksonDeserializer.class)
public class NumberOrNumberArray implements Serializable {

    private Double value;

    private List<Double> values;

    public NumberOrNumberArray(List<Double> values) {
        this.values = values;
    }

    public NumberOrNumberArray(Double value) {
        this.value = value;
    }
}
