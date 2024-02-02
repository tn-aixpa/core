package it.smartcommunitylabdhub.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.kubernetes.client.openapi.ApiException;
import it.smartcommunitylabdhub.core.components.kubernetes.K8sSecretHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class K8sSecretHelperTests {

	@Autowired
	private K8sSecretHelper helper;

	@Test
	void readNamespacedSecret() throws ApiException {
		try {
            helper.deleteSecret("test");
        } catch (ApiException e) {
        }
		
		Map<String, String> data = null;
		try {
			data = helper.getSecretData("test");
		} catch (ApiException e) {
			e.printStackTrace();
		}
		assertEquals(data, null);
	}

	@Test
	void createSecret() {
		try {
            helper.deleteSecret("test");
        } catch (ApiException e) {
        }

		// create
		Map<String, String> data = new HashMap<>();
		data.put("mykey", "myvalue");
		try {
            helper.storeSecretData("test", data);
        } catch (Exception e) {
            e.printStackTrace();
        }

		// read created
		Map<String, String> readData = null;
        try {
            readData = helper.getSecretData("test");
        } catch (ApiException e) {
            e.printStackTrace();
        }
		assertEquals(data, readData);

		data.put("mykey2", "myvalue2");
		try {
            helper.storeSecretData("test", data);
            readData = helper.getSecretData("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
		assertEquals(data.get("mykey2"), readData.get("mykey2"));
		assertEquals(data.get("mykey"), readData.get("mykey"));

		data.put("mykey", "myvalue3");
		try {
            helper.storeSecretData("test", data);
            readData = helper.getSecretData("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
		assertEquals(data.get("mykey2"), readData.get("mykey2"));
		assertEquals(data.get("mykey"), readData.get("mykey"));

		try {
			helper.deleteSecretKeys("test", Collections.singleton("mykey2"));
            readData = helper.getSecretData("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
		assertEquals(null, readData.get("mykey2"));
	}

	@AfterEach
	public void cleanUp() throws ApiException {	
		try {
            helper.deleteSecret("test");
        } catch (ApiException e) {
        }
	}
}
