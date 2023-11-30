package it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AccessorRegistry<T extends Accessor<Object>> {
    // A map to store accessor types and their corresponding classes.
    private final Map<String, Class<? extends Accessor<Object>>> accessorTypes = new HashMap<>();

    // Register accessor types along with their corresponding classes.
    public void registerAccessorTypes(Map<String, Class<? extends Accessor<Object>>> accessorTypeMap) {
        accessorTypes.putAll(accessorTypeMap);
    }

    /**
     * Create an instance of a accessor based on its type and configure it with fields.
     *
     * @param kind   The type of the accessor to create.
     * @param fields The fields used to configure the accessor.
     * @param <S>    The generic type for the accessor.
     * @return An instance of the accessor type, or null if not found or in case of errors.
     */
    public <S extends Accessor<Object>> S createAccessor(@NotNull String kind, @NotNull EntityName entity, Map<String, Object> fields) {
        // Retrieve the class associated with the accessor type.
        final String accessorKey = kind + "_" + entity.name().toLowerCase();
        return getAccessor(fields, accessorKey, entity);
    }


    @SuppressWarnings("unchecked")
    private <S extends Accessor<Object>> S getAccessor(Map<String, Object> fields, String accessorKey, EntityName entity) {

        Class<? extends T> accessorClass = (Class<? extends T>) accessorTypes.get(accessorKey);

        if (accessorClass == null) {
            final String defaultAccessorKey = entity.name().toLowerCase() + "_" + entity.name().toLowerCase();

            log.warn("Accessor not found: tried to extract accessor for <" + accessorKey + "> key." +
                    " Get default accessor instead " + "<" + defaultAccessorKey + ">");

            // Fallback accessor Default if no class accessor is found, avoid crash.
            // Field accessor can be useful but not essential
            accessorClass = (Class<? extends T>) accessorTypes.get(defaultAccessorKey);

            if (accessorClass == null) {
                throw new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Accessor not found: tried to extract accessor for <" + accessorKey + "> key" +
                                "Tried to get default accessor but failed" + "<" + defaultAccessorKey + ">",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        }

        try {
            // Create a new instance of the accessor class.
            S accessor = (S) accessorClass.getDeclaredConstructor().newInstance();
            // Configure the accessor instance with the provided data.
            if (fields != null) {
                accessor.build(fields);
            }
            return accessor;
        } catch (Exception e) {
            // Handle any exceptions that may occur during instance creation.
            log.error("Cannot configure accessor for type @AccessorType('" + accessorKey + "') no way to recover error.");
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Cannot configure accessor for type @AccessorType('" + accessorKey + "')",
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
