package it.smartcommunitylabdhub.commons.models.report;

import java.io.Serializable;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestBaseSpec extends ReportBaseSpec{

    @NotBlank
    private TestStatus status;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
        TestBaseSpec spec = mapper.convertValue(data, TestBaseSpec.class);
        this.status = spec.getStatus();
    }
}
