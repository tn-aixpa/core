package it.smartcommunitylabdhub.framework.k8s.exceptions;

import it.smartcommunitylabdhub.commons.exceptions.FrameworkException;

public class K8sFrameworkException extends FrameworkException {

    public static final String DEFAULT_MESSAGE = "Error with the Kubernetes API.";
    private final String apiResponse;

    public K8sFrameworkException() {
        super(DEFAULT_MESSAGE);
        this.apiResponse = null;
    }

    public K8sFrameworkException(String msg) {
        super(msg);
        this.apiResponse = null;
    }

    public K8sFrameworkException(String msg, String apiResponse) {
        super(msg);
        this.apiResponse = apiResponse;
    }

    public K8sFrameworkException(String msg, Throwable cause) {
        super(msg, cause);
        this.apiResponse = null;
    }

    public String getApiResponse() {
        return apiResponse;
    }

    public String toError() {
        StringBuilder sb = new StringBuilder("k8s: ");
        if (getMessage() != null) {
            sb.append(getMessage()).append(" ");
        }
        if (getApiResponse() != null) {
            sb.append(getApiResponse()).append(" ");
        }
        return sb.toString();
    }
}
