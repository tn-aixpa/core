package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;


import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@SpecType(kind = "table", entity = EntityName.DATAITEM)
public class DataItemTableSpec extends DataItemBaseSpec<DataItemTableSpec> {
    @Override
    protected void configureSpec(DataItemTableSpec dataItemTableSpec) {
        super.configureSpec(dataItemTableSpec);

        throw new CoreException(
                ErrorList.METHOD_NOT_IMPLEMENTED.getValue(),
                ErrorList.METHOD_NOT_IMPLEMENTED.getReason(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
