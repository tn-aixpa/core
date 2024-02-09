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
import jakarta.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public record PatchBody(Object value, String path) {
        private static final String REPLACE_OPERATION = "replace";

        public String getOp() {
            return REPLACE_OPERATION;
        }
    }
}
