package it.smartcommunitylabdhub.commons.models.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SourceCode<T extends Enum<?>> implements Serializable {

    @Nullable
    @Schema(title = "fields.sourceCode.source.title", description = "fields.sourceCode.source.description")
    private String source;

    @Nullable
    @Schema(title = "fields.sourceCode.handler.title", description = "fields.sourceCode.handler.description")
    private String handler;

    @Schema(title = "fields.sourceCode.base64.title", description = "fields.sourceCode.base64.description")
    private String base64;

    @Schema(title = "fields.sourceCode.lang.title", description = "fields.sourceCode.lang.description")
    private T lang;
}
