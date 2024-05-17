package it.smartcommunitylabdhub.commons.models.entities.log;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@SpecType(kind = "log", entity = EntityName.LOG)
public class LogSpec extends LogBaseSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
