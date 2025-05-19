package it.smartcommunitylabdhub.authorization.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {

    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Integer expiration;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("issuer")
    private String issuer;

    @JsonProperty("issued_token_type")
    private String issuedTokenType;

    public void setIssuedTokenType(String issuedTokenType) {
        this.issuedTokenType = issuedTokenType;
    }

    @JsonIgnore
    private Map<String, String> credentials;

    @JsonAnyGetter
    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    @JsonAnySetter
    public void setCredentials(String key, String value) {
        if (this.credentials == null) {
            this.credentials = new HashMap<>();
        }

        this.credentials.put(key, value);
    }
}
