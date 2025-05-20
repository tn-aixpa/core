package it.smartcommunitylabdhub.core.controllers;

import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.core.services.ConfigurationService;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationEndpoint {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Value("${jwt.cache-control}")
    private String cacheControl;

    //cache, we don't expect config to be mutable!
    private Map<String, Serializable> config = null;

    @GetMapping(value = { "/.well-known/configuration" })
    public ResponseEntity<Map<String, Serializable>> getConfiguration() {
        if (config == null) {
            config = generate();
        }

        return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, cacheControl).body(config);
    }

    private Map<String, Serializable> generate() {
        Map<String, Serializable> map = new HashMap<>();
        if (configurationService != null) {
            configurationService.getConfigurations().forEach(c -> map.putAll(c.toMap()));
        }

        //always override core props
        map.put("dhcore_endpoint", applicationProperties.getEndpoint());

        return Collections.unmodifiableMap(map);
    }
}
