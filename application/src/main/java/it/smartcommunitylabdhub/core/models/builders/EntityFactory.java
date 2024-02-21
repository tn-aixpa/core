package it.smartcommunitylabdhub.core.models.builders;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityFactory {


    public static <T, U> T create(
            Supplier<T> entitySupplier,
            Consumer<EntityBuilder<T, U>> buildFunction
    ) {
        EntityBuilder<T, U> builder = new EntityBuilder<>(entitySupplier);
        buildFunction.accept(builder);
        return builder.build();
    }


    public static <T, U> T combine(
            T sourceEntity,
            Consumer<EntityBuilder<T, U>> buildFunction
    ) {
        Supplier<T> entitySupplier = () -> sourceEntity;
        return create(entitySupplier, buildFunction);
    }
}
