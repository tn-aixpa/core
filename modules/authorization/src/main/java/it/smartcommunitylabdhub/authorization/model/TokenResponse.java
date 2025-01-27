package it.smartcommunitylabdhub.authorization.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jwt.SignedJWT;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse extends AbstractCredentials {

    @JsonProperty("access_token")
    private SignedJWT accessToken;

    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Integer expiration;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("issuer")
    private String issuer;

    //TODO evaluate unwrap
    @JsonProperty("credentials")
    private Map<String, String> credentials;

    @JsonGetter
    public String getAccessToken() {
        return accessToken != null ? accessToken.serialize() : null;
    }
}
