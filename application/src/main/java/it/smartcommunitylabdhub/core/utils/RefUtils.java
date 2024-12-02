package it.smartcommunitylabdhub.core.utils;

import it.smartcommunitylabdhub.commons.accessors.fields.KeyAccessor;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import jakarta.annotation.Nullable;
import org.springframework.util.StringUtils;

public class RefUtils {

    public static @Nullable String getRefPath(BaseDTO dto) {
        KeyAccessor key = KeyAccessor.with(dto.getKey());

        if (!StringUtils.hasText(key.getId())) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("/-/");
        sb.append(key.getProject()).append("/");
        sb.append(key.getType()).append("s").append("/");
        sb.append(key.getId());

        return sb.toString();
    }

    private RefUtils() {}
}
