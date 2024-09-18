package it.smartcommunitylabdhub.framework.k8s.objects.envoy;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoreProxyStat implements Serializable {

    private String pod;
    private String timestamp;
    private String window;
    private List<Stat> stats;
    private String namespace;

}