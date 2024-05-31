package it.smartcommunitylabdhub.framework.k8s.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContextRef implements Serializable {

    private String destination;
    private String protocol;
    private String source;

    public static ContextRef from(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }

        try {
            //parse as URI to extract protocol
            UriComponents uri = UriComponentsBuilder.fromUriString(source).build();
            String scheme = uri.getScheme();
            String path = uri.getPath();

            return ContextRef.builder().protocol(scheme).source(source).destination(path).build();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
