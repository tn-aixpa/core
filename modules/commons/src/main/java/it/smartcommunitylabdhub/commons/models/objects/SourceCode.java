package it.smartcommunitylabdhub.commons.models.objects;

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
public class SourceCode implements Serializable {

    @Nullable
    @Schema(description = "Source reference")
    private String source;

    @Schema(description = "Source code (plain)")
    private String code;

    @Schema(description = "Source code (base64 encoded)")
    private String base64;

    @Schema(description = "Source code language (hint)")
    private String lang;
}
