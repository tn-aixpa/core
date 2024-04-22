package it.smartcommunitylabdhub.framework.k8s.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoreItems implements Serializable {

    private List<Map<String, Serializable>> coreItems;
    

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Map<String, Serializable>> coreItems = new ArrayList<>();

        public Builder keyToPath(Map<String, Serializable> keyToPath) {
            this.coreItems.add(keyToPath);
            return this;
        }

        public Builder keyToPath(List<Map<String, Serializable>> keyToPath) {
            this.coreItems.addAll(keyToPath);
            return this;
        }

        public CoreItems build() {
            return new CoreItems(coreItems);
        }
    }
}
