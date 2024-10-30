package it.smartcommunitylabdhub.core.utils;

import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.accessors.fields.KeyAccessor;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyUtils {

    private static final Pattern KEY_PATTERN = Pattern.compile(Keys.KEY_PATTERN);

    public static String getKey(BaseDTO dto, String entityType) {
        StringBuilder sb = new StringBuilder();
        sb.append(Keys.STORE_PREFIX).append(dto.getProject());
        sb.append(Keys.PATH_DIVIDER).append(entityType);
        sb.append(Keys.PATH_DIVIDER).append(dto.getKind());
        sb.append(Keys.PATH_DIVIDER).append(dto.getName());
        if (dto.getId() != null) {
            sb.append(Keys.ID_DIVIDER).append(dto.getId());
        }

        return sb.toString();
    }

    public static String getKey(BaseEntity entity, String entityType) {
        StringBuilder sb = new StringBuilder();
        sb.append(Keys.STORE_PREFIX).append(entity.getProject());
        sb.append(Keys.PATH_DIVIDER).append(entityType);
        sb.append(Keys.PATH_DIVIDER).append(entity.getKind());
        sb.append(Keys.PATH_DIVIDER).append(entity.getName());
        if (entity.getId() != null) {
            sb.append(Keys.ID_DIVIDER).append(entity.getId());
        }

        return sb.toString();
    }

    public static KeyAccessor parseKey(String key) {
        if (key == null || key.isEmpty() || !key.startsWith(Keys.STORE_PREFIX)) {
            return KeyAccessor.with(Collections.emptyMap());
        }

        Matcher matcher = KEY_PATTERN.matcher(key);
        if (matcher.matches()) {
            String project = matcher.group(1);
            String type = matcher.group(2);
            String kind = matcher.group(3);
            String name = matcher.group(4);
            String id = matcher.groupCount() == 5 ? matcher.group(5) : null;

            Map<String, String> map = new HashMap<>();
            map.put("project", project);
            map.put("type", type);
            map.put("kind", kind);
            map.put("name", name);
            map.put("id", id);

            return KeyAccessor.with(map);
        }
        throw new IllegalArgumentException("Cannot create accessor for the given task string.");
    }

    private KeyUtils() {}
}
