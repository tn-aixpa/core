package it.smartcommunitylabdhub.core.components.kubernetes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;


@Component
public class K8sSecretHelper {

    private final CoreV1Api api;

    public K8sSecretHelper(ApiClient client) {
        api = new CoreV1Api(client);
    }

    @Value("${kubernetes.namespace}")
    private String namespace;

    public Map<String, String> getSecretData(String secretName) throws ApiException {

        V1Secret secret = api.readNamespacedSecret(secretName, namespace, ""); 
        if (secret != null) {
            Map<String, byte[]> secretData = secret.getData();
            Map<String, String> writeData = new HashMap<>();
            for (String key: secretData.keySet()) {
                String enc = new String(secretData.get(key), StandardCharsets.UTF_8);
                writeData.put(key, enc);
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
            keys.forEach(key -> {
                if (secretData.containsKey(key)) secretData.remove(key);
            });
            Map<String, String> writeData = new HashMap<>();
            for (String key: secretData.keySet()) {
                String enc = new String(secretData.get(key), StandardCharsets.UTF_8);
                writeData.put(key, enc);
            }
            PatchBody pathchData = new PatchBody(writeData, "/data");
            V1Patch patch = new V1Patch(mapper.writeValueAsString(Collections.singleton(pathchData)));

            api.patchNamespacedSecret(secretName, namespace, patch, null, null, null, null, null); 
        }

    }

    public void storeSecretData(String secretName, Map<String, String> data) throws JsonProcessingException, ApiException {
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
                if (data.containsKey(key)) writeData.put(key, Base64.getEncoder().encodeToString(data.get(key).getBytes(StandardCharsets.UTF_8)));
            }
            for (String key: secretData.keySet()) {
                if (!writeData.containsKey(key)) writeData.put(key, new String(secretData.get(key), StandardCharsets.UTF_8));
            }
            PatchBody pathchData = new PatchBody(writeData, "/data");
            V1Patch patch = new V1Patch(mapper.writeValueAsString(Collections.singleton(pathchData)));
            api.patchNamespacedSecret(secretName, namespace, patch, null, null, null, null, null); 
        } else {
            V1Secret body = new V1Secret(); 
            body.setMetadata(new V1ObjectMeta());
            body.getMetadata().setName(secretName);
            body.getMetadata().setNamespace(namespace);
            body.setApiVersion("v1");
            body.setKind("Secret");
            body.setStringData(data);
            api.createNamespacedSecret(namespace, body, null, null, null, null);
        }
    }

    public class PatchBody {
        private static final String REPLACE_OPERATION = "replace";
        private final Object value;
        private final String path;

        public PatchBody(Object value, String path) {
            this.value = value;
            this.path = path;
        }

        public String getOp() {
            return REPLACE_OPERATION;
        }

        public String getPath() {
            return path;
        }

        public Object getValue() {
            return value;
        }
    }
}
