package it.smartcommunitylabdhub.authorization.config;

import com.nimbusds.jose.JOSEException;
import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class KeyStoreConfig {

    @Value("${jwt.keystore.path}")
    private Resource location;

    @Value("${jwt.keystore.kid}")
    private String kid;

    private JWKSetKeyStore keyStore;

    @Bean
    public JWKSetKeyStore getJWKSetKeyStore() throws JOSEException {
        if (keyStore != null) {
            //re-use because we load from config *before* services are built
            return keyStore;
        }

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
