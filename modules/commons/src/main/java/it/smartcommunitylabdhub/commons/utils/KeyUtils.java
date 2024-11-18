package it.smartcommunitylabdhub.commons.utils;

import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;

public class KeyUtils {

    // private static final Pattern KEY_PATTERN = Pattern.compile(Keys.KEY_PATTERN);
    // private static final Pattern KEY_PATTERN_NO_ID = Pattern.compile(Keys.KEY_PATTERN_NO_ID);

    public static String buildKey(String project, String entityType, String kind, String name, String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(Keys.STORE_PREFIX).append(project);
        sb.append(Keys.PATH_DIVIDER).append(entityType);
        sb.append(Keys.PATH_DIVIDER).append(kind);
        sb.append(Keys.PATH_DIVIDER).append(name);
        if (id != null) {
            sb.append(Keys.ID_DIVIDER).append(id);
        }

        return sb.toString();
    }

    public static String getKey(BaseDTO dto, String entityType) {
        return buildKey(dto.getProject(), entityType, dto.getKind(), dto.getName(), dto.getId());
    }

    // public static KeyAccessor parseKey(String key) {
    //     if (key == null || key.isEmpty() || !key.startsWith(Keys.STORE_PREFIX)) {
    //         return KeyAccessor.with(Collections.emptyMap());
    //     }

    //     //match full key first
    //     Matcher matcher = KEY_PATTERN.matcher(key);
    //     if (!matcher.matches()) {
    //         //fallback to partial key
    //         matcher = KEY_PATTERN_NO_ID.matcher(key);
    //     }

    //     if (matcher.matches()) {
    //         String project = matcher.group(1);
    //         String type = matcher.group(2);
    //         String kind = matcher.group(3);
    //         String name = matcher.group(4);
    //         String id = matcher.groupCount() == 5 ? matcher.group(5) : null;

    //         Map<String, String> map = new HashMap<>();
    //         map.put("project", project);
    //         map.put("type", type);
    //         map.put("kind", kind);
    //         map.put("name", name);
    //         map.put("id", id);

    //         return KeyAccessor.with(map);
    //     }
    //     throw new IllegalArgumentException("Cannot create accessor for the given task string.");
    // }

    private KeyUtils() {}
}
