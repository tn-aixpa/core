package it.smartcommunitylabdhub.framework.k8s.kubernetes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnKubernetes
public class K8sSecretHelper {

    private final CoreV1Api api;

    @Value("${kubernetes.namespace}")
    private String namespace;

    public K8sSecretHelper(ApiClient client) {
        api = new CoreV1Api(client);
    }

    public Map<String, String> getSecretData(String secretName) throws ApiException {
        V1Secret secret = api.readNamespacedSecret(secretName, namespace, "");
        if (secret != null) {
            Map<String, byte[]> secretData = secret.getData();
            Map<String, String> writeData = new HashMap<>();

            if (secretData != null) {
                for (String key : secretData.keySet()) {
                    String enc = new String(secretData.get(key), StandardCharsets.UTF_8);
                    writeData.put(key, enc);
                }
            }

            return writeData;
        }
        return new HashMap<>();
    }

    public void deleteSecret(String secretName) throws ApiException {
        try {
            V1Secret secret = api.readNamespacedSecret(secretName, namespace, "");
        } catch (ApiException e) {
            return;
        }

        //delete
        api.deleteNamespacedSecret(secretName, namespace, null, null, 0, null, "Foreground", new V1DeleteOptions());
    }

    public void deleteSecretKeys(String secretName, Set<String> keys) throws JsonProcessingException, ApiException {
        V1Secret secret;
        try {
            secret = api.readNamespacedSecret(secretName, namespace, "");
        } catch (ApiException e) {
            return;
        }
        if (secret != null) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, byte[]> secretData = secret.getData();
            if (secretData == null) return;

            keys.forEach(secretData::remove);

            Map<String, String> writeData = new HashMap<>();
            for (String key : secretData.keySet()) {
                if (!writeData.containsKey(key)) writeData.put(
                    key,
                    Base64.getEncoder().encodeToString(secretData.get(key))
                );
            }
            PatchBody patchData = new PatchBody(writeData, "/data");
            V1Patch patch = new V1Patch(mapper.writeValueAsString(Collections.singleton(patchData)));

            api.patchNamespacedSecret(secretName, namespace, patch, null, null, null, null, null);
        }
    }

    public void storeSecretData(@NotNull String secretName, Map<String, String> data)
        throws JsonProcessingException, ApiException {
        V1Secret secret;
        try {
            secret = api.readNamespacedSecret(secretName, namespace, "");
        } catch (ApiException e) {
            // secret does not exist, create
            secret = null;
        }
        if (secret != null) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, byte[]> secretData = secret.getData();
            Map<String, String> writeData = new HashMap<>();
            for (String key : data.keySet()) {
                writeData.put(key, Base64.getEncoder().encodeToString(data.get(key).getBytes(StandardCharsets.UTF_8)));
            }

            if (secretData != null) {
                for (String key : secretData.keySet()) {
                    if (!writeData.containsKey(key)) writeData.put(
                        key,
                        Base64.getEncoder().encodeToString(secretData.get(key))
                    );
                }
            }

            PatchBody patchData = new PatchBody(writeData, "/data");
            V1Patch patch = new V1Patch(mapper.writeValueAsString(Collections.singleton(patchData)));
            api.patchNamespacedSecret(secretName, namespace, patch, null, null, null, null, null);
        } else {
            V1Secret body = new V1Secret()
                .metadata(new V1ObjectMeta().name(secretName).namespace(namespace))
                .apiVersion("v1")
                .kind("Secret")
                .stringData(data);
            api.createNamespacedSecret(namespace, body, null, null, null, null);
        }
    }

    public @Nullable V1Secret convertAuthentication(String name, AbstractAuthenticationToken auth) {
        if (auth instanceof JwtAuthenticationToken) {
            Jwt token = ((JwtAuthenticationToken) auth).getToken();
            if (token == null) {
                throw new IllegalArgumentException("missing token");
            }

            String jwt = token.getTokenValue();
            String sub = token.getSubject();
            String username = token.getClaimAsString("preferred_username");

            Map<String, String> data = new HashMap<>();
            data.put("DH_AUTH_TOKEN", jwt);
            data.put("DH_AUTH_SUB", sub);
            data.put("DH_USERNAME", StringUtils.hasText(username) ? username : sub);

            return new V1Secret()
                .metadata(new V1ObjectMeta().name(name).namespace(namespace))
                .apiVersion("v1")
                .kind("Secret")
                .stringData(data);
        }

        return null;
    }

    // Generate and return job name
    public String getSecretName(String runtime, String task, String id) {
        return "sec" + "-" + runtime + "-" + task + "-" + id;
    }

    public record PatchBody(Object value, String path) {
        private static final String REPLACE_OPERATION = "replace";

        public String getOp() {
            return REPLACE_OPERATION;
        }
    }
}
