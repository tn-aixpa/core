package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import java.util.Map;

/**
 * Define base accessor
 */
public interface Accessor<T> {

    /**
     * Return a map of fields
     *
     * @return {@code Map} of claims
     */
    Map<String, T> fields();

    /**
     * build the map of fields
     *
     * @param fields {@code Map} of claims
     */
    void build(Map<String, Object> fields);

    /**
     * Returns the claim value as a {@code T} type. The claim value is expected to be of type
     * {@code T}.
     *
     * @param field the name of the field
     * @return
     */
    default T getField(String field) {
        return !hasField(field) ? null : fields().get(field);
    }

    /**
     * Check if field exists.
     *
     * @param field
     * @return {@code true} if the field exists, otherwise {@code false}
     */
    default boolean hasField(String field) {
        return fields().containsKey(field);
    }

    /**
     * Given a map and a field check if field exists.
     *
     * @param field
     * @param map
     * @return {@code true} if the field exists, otherwise {@code false}
     */
    default boolean mapHasField(Map<String, T> map, String field) {
        return map.containsKey(field);
    }
}
