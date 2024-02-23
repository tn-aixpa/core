package it.smartcommunitylabdhub.framework.k8s.exceptions;

import it.smartcommunitylabdhub.commons.exceptions.FrameworkException;

public class K8sFrameworkException extends FrameworkException {

    public static final String DEFAULT_MESSAGE = "Error with the Kubernetes API.";

    public K8sFrameworkException() {
        super(DEFAULT_MESSAGE);
    }

    public K8sFrameworkException(String msg) {
        super(msg);
    }

    public K8sFrameworkException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
