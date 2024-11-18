package it.smartcommunitylabdhub.runtime.sklearn.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.model.ModelSpec;

@SpecType(kind = "sklearn", entity = EntityName.MODEL)
public class SKLearnModelSpec extends ModelSpec {}
