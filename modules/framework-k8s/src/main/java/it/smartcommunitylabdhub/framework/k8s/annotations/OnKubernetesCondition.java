package it.smartcommunitylabdhub.framework.k8s.annotations;

import io.kubernetes.client.util.ClientBuilder;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnKubernetesCondition implements Condition {

  @Override
  public boolean matches(
    ConditionContext context,
    AnnotatedTypeMetadata metadata
  ) {
    //check if auto-detected first
    if (CloudPlatform.KUBERNETES.isActive(context.getEnvironment())) {
      return true;
    }

    //fall back to local client creation
    try {
      ClientBuilder.standard().build();
      return true;
    } catch (Exception e1) {
      return false;
    }
  }
}
