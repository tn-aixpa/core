package it.smartcommunitylabdhub.core.controllers.v2;

import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.artifacts.ArtifactDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.dataitems.DataitemDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// TODO: put as example in documentation.
@RestController
@RequestMapping("/projects")
@ApiVersion("v2")
@Slf4j
public class ProjectControllerV2Test {

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;

    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> myVersioned2Json() {

        DataitemDefaultFieldAccessor a = accessorRegistry.createAccessor(
                "dataitem",
                EntityName.DATAITEM,
                Map.of()
        );
        log.info(a.getName());

        ArtifactDefaultFieldAccessor b = accessorRegistry.createAccessor(
                "artifact",
                EntityName.ARTIFACT,
                Map.of("name", "Ciccio", "project", "Franco")
        );
        return ResponseEntity.ok(b.fields());
    }

}
