package it.smartcommunitylabdhub.core.models.base.specs;


import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@SpecType(kind = "none", entity = EntityName.NONE, factory = NoneNoneSpec.class)
public class NoneNoneSpec extends NoneBaseSpec<NoneNoneSpec> {
    @Override
    protected void configureSpec(NoneNoneSpec noneNoneSpec) {
        super.configureSpec(noneNoneSpec);

    }
}
