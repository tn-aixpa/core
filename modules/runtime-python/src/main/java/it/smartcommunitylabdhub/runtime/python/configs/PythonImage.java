package it.smartcommunitylabdhub.runtime.python.configs;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@ConfigurationProperties(prefix = "runtime.python")
public class PythonImage {

    private Map<String, String> images;

    public void setImages(Map<String, String> images) {
        this.images = images;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public String getImage(String version) {
        return images.get(version);
    }
}
