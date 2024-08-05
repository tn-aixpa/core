package it.smartcommunitylabdhub.authorization;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import it.smartcommunitylabdhub.authorization.utils.JWKUtils;
import org.junit.jupiter.api.Test;

public class JwkTests {

    @Test
    public void generateRsaKey() throws Exception {
        JWK jwk = JWKUtils.generateRsaJWK(null);
        assertThat(jwk).isNotNull();
        //validate content
        assertThat(jwk.getKeyID()).isNotBlank();
        assertThat(jwk.getAlgorithm()).isEqualTo(JWSAlgorithm.RS256);
        assertThat(jwk.getKeyUse()).isEqualTo(KeyUse.SIGNATURE);
    }
}
