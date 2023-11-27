package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.XRun;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RunService {

    List<XRun> getRuns(Pageable pageable);

    XRun getRun(String uuid);

    boolean deleteRun(String uuid);

    XRun save(XRun runDTO);

    <F extends FunctionBaseSpec<F>> XRun createRun(XRun inputRunDTO);

    XRun updateRun(@Valid XRun runDTO, String uuid);

}
