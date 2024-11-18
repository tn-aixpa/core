package it.smartcommunitylabdhub.commons.accessors.fields;

import io.micrometer.common.lang.Nullable;
import it.smartcommunitylabdhub.commons.Fields;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.accessors.Accessor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Status field common accessor
 */
public interface KeyAccessor extends Accessor<String> {
    default @Nullable String getProject() {
        return get(Fields.PROJECT);
    }
    default @Nullable String getType() {
        return get(Fields.TYPE);
    }

    default @Nullable String getKind() {
        return get(Fields.KIND);
    }

    default @Nullable String getName() {
        return get(Fields.NAME);
    }

    default @Nullable String getId() {
        return get(Fields.ID);
    }

    static KeyAccessor with(Map<String, String> map) {
        return () -> map;
    }

    static KeyAccessor with(String key) {
        if (key == null || key.isEmpty() || !key.startsWith(Keys.STORE_PREFIX)) {
            return KeyAccessor.with(Collections.emptyMap());
        }

        //match full key first
        Matcher matcher = Pattern.compile(Keys.KEY_PATTERN).matcher(key);
        if (!matcher.matches()) {
            //fallback to partial key
            matcher = Pattern.compile(Keys.KEY_PATTERN_NO_ID).matcher(key);
        }

        if (matcher.matches()) {
            String project = matcher.group(1);
            String type = matcher.group(2);
            String kind = matcher.group(3);
            String name = matcher.group(4);
            String id = matcher.groupCount() == 5 ? matcher.group(5) : null;

            Map<String, String> map = new HashMap<>();
            map.put(Fields.PROJECT, project);
            map.put(Fields.TYPE, type);
            map.put(Fields.KIND, kind);
            map.put(Fields.NAME, name);
            map.put(Fields.ID, id);

            return KeyAccessor.with(map);
        }
        throw new IllegalArgumentException("Cannot create accessor for the given task string.");
    }
}
