package it.smartcommunitylabdhub.authorization.config;

import com.nimbusds.jose.JOSEException;
import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

@Configuration
public class KeyStoreConfig {

    @Value("${jwt.keystore.path}")
    private String path;

    @Value("${jwt.keystore.kid}")
    private String kid;

    @Autowired
    private ResourceLoader resourceLoader;

    private JWKSetKeyStore keyStore;

    @Bean
    public JWKSetKeyStore getJWKSetKeyStore() throws JOSEException {
        if (keyStore != null) {
            //re-use because we load from config *before* services are built
            return keyStore;
        }

        if (StringUtils.hasText(path) && !path.contains(":")) {
            //no protocol specified, try as file by default
            this.path = "file:" + path;
        }

        Resource location = resourceLoader.getResource(path);
        if (location != null && location.exists() && location.isReadable()) {
            // Load from resource
            keyStore = new JWKSetKeyStore(location, kid);
        } else {
            // Generate new in-memory keystore
            keyStore = new JWKSetKeyStore();
        }

        return keyStore;
    }
}
