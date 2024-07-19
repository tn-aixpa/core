package it.smartcommunitylabdhub.framework.k8s.objects;

import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoreResourceDefinition implements Serializable {

    @Pattern(regexp = "[\\d]+|^([0-9])+([a-zA-Z])+$")
    private String requests;

    @Pattern(regexp = "[\\d]+|^([0-9])+([a-zA-Z])+$")
    private String limits;
}
