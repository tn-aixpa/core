package it.smartcommunitylabdhub.core.models.builders;

import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityFactory {

    //deprecated because entity is never used, just a wrapper around entityBuilder
    @Deprecated(forRemoval = true)
    public static <T extends BaseEntity, U extends BaseEntity> T create(
        Supplier<T> entitySupplier,
        U entity,
        Consumer<EntityBuilder<T, U>> buildFunction
    ) {
        EntityBuilder<T, U> builder = new EntityBuilder<>(entitySupplier);
        buildFunction.accept(builder);
        return builder.build();
    }

    //deprecated because entity is never used, just a wrapper around entityBuilder
    @Deprecated(forRemoval = true)
    public static <T extends BaseEntity, U extends BaseEntity> T combine(
        T sourceEntity,
        U entity,
        Consumer<EntityBuilder<T, U>> buildFunction
    ) {
        Supplier<T> entitySupplier = () -> sourceEntity;
        return create(entitySupplier, entity, buildFunction);
    }
}
