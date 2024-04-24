package it.smartcommunitylabdhub.core.components.security;

import java.io.Serializable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;

public class SecureCredentialsHelper {

    public static Serializable extractCredentials(Authentication auth) {
        if (auth == null) {
            return null;
        }

        if (auth instanceof JwtAuthenticationToken) {
            return convertCredentials((JwtAuthenticationToken) auth);
        }

        return null;
    }

    public static Serializable convertCredentials(JwtAuthenticationToken auth) {
        Assert.notNull(auth, "auth token can not be null");
        Assert.notNull(auth.getToken(), "jwt token can not be null");

        //token value is the credential
        return auth.getToken();
    }

    private SecureCredentialsHelper() {}
}
