package it.smartcommunitylabdhub.core.models.entities.function.specs;

import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunctionBaseSpec<S extends FunctionBaseSpec<S>> extends BaseSpec<S> {

    @Override
    protected void configureSpec(S concreteSpec) {
        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
