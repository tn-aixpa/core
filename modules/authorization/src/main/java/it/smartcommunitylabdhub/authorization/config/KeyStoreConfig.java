package it.smartcommunitylabdhub.authorization.config;


import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import it.smartcommunitylabdhub.authorization.utils.JWKUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


@Configuration
public class KeyStoreConfig {

    @Value("${keystore.path:}")
    private Resource path;

    @Value("${keystore.kid:}")
    private String kid;

    private JWKSetKeyStore keyStore;


    @Bean
    @Primary
    public JWKSetKeyStore getJWKSetKeyStore() throws JOSEException {
        if (keyStore == null) {
            if (path != null) {
                // load from resource
                keyStore = load(path);
                // check if empty
                if (keyStore.getJwk() == null) {
                    // discard, we will generate a new one
                    keyStore = null;
                }
            }
        }

        if (keyStore == null) {
            // generate new in-memory keystore
            keyStore = generate();
        }
        return keyStore;
    }

    private JWKSetKeyStore load(Resource location) {
        return new JWKSetKeyStore(location, kid);
    }

    private JWKSetKeyStore generate() throws JOSEException {
        return new JWKSetKeyStore();
    }
}
