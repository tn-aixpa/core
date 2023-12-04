package it.smartcommunitylabdhub.core.models.builders;

import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityBuilder<T extends BaseEntity, U extends BaseEntity> {
    private T result;

    public EntityBuilder(Supplier<T> entitySupplier) {
        this.result = entitySupplier.get();
    }

    public EntityBuilder<T, U> with(Consumer<T> fieldSetter) {
        fieldSetter.accept(result);
        return this;
    }

    public EntityBuilder<T, U> withIf(boolean condition, Consumer<T> fieldSetter) {
        if (condition) {
            fieldSetter.accept(result);
        }
        return this;
    }

    public EntityBuilder<T, U> withIfElse(boolean condition, BiConsumer<T, Boolean> fieldSetter) {
        fieldSetter.accept(result, condition);
        return this;
    }

    public EntityBuilder<T, U> withIfElse(boolean condition, Consumer<T> ifSetter, Consumer<T> elseSetter) {
        if (condition) {
            ifSetter.accept(result);
        } else {
            elseSetter.accept(result);
        }
        return this;
    }

    public T build() {
        return result;
    }
}
