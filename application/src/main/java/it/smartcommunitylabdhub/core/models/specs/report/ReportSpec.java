package it.smartcommunitylabdhub.core.models.specs.report;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.report.ReportBaseSpec;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "report", entity = EntityName.REPORT)
public class ReportSpec extends ReportBaseSpec {}
