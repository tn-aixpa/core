package it.smartcommunitylabdhub.framework.k8s.processors;

import it.smartcommunitylabdhub.commons.annotations.common.RunProcessorType;
import it.smartcommunitylabdhub.commons.infrastructure.RunProcessor;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.framework.k8s.model.K8sServiceStatus;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@RunProcessorType(stages = { "onRunning" }, id = K8sServiceProcessor.ID)
@Component(K8sServiceProcessor.ID)
public class K8sServiceProcessor implements RunProcessor<RunBaseStatus> {

    public static final String ID = "k8sServiceProcessor";

    @Override
    public RunBaseStatus process(Run run, RunRunnable runRunnable, RunBaseStatus baseStatus) {
        if (runRunnable instanceof K8sRunnable) {
            Map<String, Serializable> res = ((K8sRunnable) runRunnable).getResults();
            //extract k8s details for svc
            if (res != null && res.containsKey("service")) {
                try {
                    Map<String, Serializable> s = (Map<String, Serializable>) res.get("service");

                    if (!s.containsKey("metadata") || !s.containsKey("spec") || !s.containsKey("status")) {
                        //missing fields
                        return null;
                    }

                    Map<String, Serializable> metadata = (Map<String, Serializable>) s.get("metadata");
                    Map<String, Serializable> spec = (Map<String, Serializable>) s.get("spec");

                    //build as map
                    //TODO define proper struct
                    HashMap<String, Serializable> svc = new HashMap<>();
                    svc.put("name", metadata.getOrDefault("name", ""));
                    svc.put("namespace", metadata.getOrDefault("namespace", null));

                    svc.put("type", spec.getOrDefault("type", null));
                    svc.put("clusterIP", spec.getOrDefault("clusterIP", null));
                    svc.put("ports", spec.get("ports"));

                    //try building urls
                    if (spec.get("ports") != null) {
                        List<Serializable> ps = (List<Serializable>) spec.get("ports");
                        if (!ps.isEmpty()) {
                            Map<String, Serializable> ports = (Map<String, Serializable>) ps.get(0);

                            Integer port = (Integer) ports.getOrDefault("port", 0);

                            String clusterIp = (String) spec.getOrDefault("clusterIP", "");
                            String name = (String) metadata.getOrDefault("name", "");
                            String namespace = (String) metadata.getOrDefault("namespace", "");

                            String url = String.format("%s.%s:%d", name, namespace, port);
                            svc.put("url", url);

                            String ip = String.format("%s:%d", clusterIp, port);
                            svc.put("ip", ip);
                        }
                    }

                    return K8sServiceStatus.builder().service(svc).build();
                } catch (ClassCastException | NullPointerException e) {
                    //invalid definition, skip
                }
            }
        }

        return null;
    }
}
