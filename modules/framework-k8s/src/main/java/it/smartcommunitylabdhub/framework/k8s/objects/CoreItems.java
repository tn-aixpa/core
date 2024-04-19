package it.smartcommunitylabdhub.framework.k8s.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class CoreItems implements Serializable {

    private final List<Map<String, Serializable>> items;

    private CoreItems(List<Map<String, Serializable>> items) {
        this.items = items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Map<String, Serializable>> items = new ArrayList<>();

        public Builder keyToPath(Map<String, Serializable> keyToPath) {
            this.items.add(keyToPath);
            return this;
        }

        public Builder keyToPath(List<Map<String, Serializable>> keyToPath) {
            this.items.addAll(keyToPath);
            return this;
        }

        public CoreItems build() {
            return new CoreItems(items);
        }
    }
}
