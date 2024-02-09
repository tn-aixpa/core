package it.smartcommunitylabdhub.commons.utils;

public enum ErrorList {
    INTERNAL_SERVER_ERROR("InternalServerError", "Internal Server Error"),

    /**
     * FUNCTION
     */
    FUNCTION_NOT_FOUND("FunctionNotFound", "The function you are searching for does not exist."),
    /**
     *
     */
    FUNCTION_NOT_MATCH(
        "FunctionNotMatch",
        "Trying to create/update a function with an uuid different from the one passed in the request."
    ),
    /**
     *
     */
    DUPLICATE_FUNCTION("DuplicateFunction", "Cannot create function."),

    /**
     * PROJECT
     */
    PROJECT_NOT_FOUND("ProjectNotFound", "The project you are searching for does not exist."),
    /**
     *
     */
    PROJECT_NOT_MATCH(
        "ProjectNotMatch",
        "Trying to create/update a project with a UUID different from the one passed in the request."
    ),
    /**
     *
     */
    DUPLICATE_PROJECT("DuplicateProjectIdOrName", "Cannot create the project, duplicated Id or Name"),

    /**
     *
     */
    RUN_NOT_FOUND("RunNotFound", "The run you are searching for does not exist."),
    /**
     *
     */
    RUN_NOT_MATCH(
        "RunNotMatch",
        "Trying to create/update a run with an uuid different from the one passed in the request."
    ),
    /**
     *
     */
    DUPLICATE_RUN("DuplicateRun", "Run already exist, use different uuid."),
    /**
     *
     */
    TASK_NOT_FOUND("TaskNotFound", "The Task you are searching for does not exist."),
    /**
     *
     */
    TASK_NOT_MATCH(
        "TaskNotMatch",
        "Trying to create/update a task with an uuid different from the one passed in the request."
    ),
    /**
     *
     */
    DUPLICATE_TASK("DuplicateTaskId", "Cannot create the task."),

    RUN_JOB_ERROR("K8sJobError", "Cannot execute job in Kubernetes"),

    /**
     * SECRET
     */
    SECRET_NOT_FOUND("SecretNotFound", "The secret you are searching for does not exist."),
    DUPLICATE_SECRET("DuplicateSecret", "Cannot create secret."),
    SECRET_NOT_MATCH(
        "SecretNotMatch",
        "Trying to create/update a secret with an uuid different from the one passed in the request."
    ),

    /**
     *
     */
    METHOD_NOT_IMPLEMENTED("MethodNotImplemented", "Method not implemented!!!");

    private final String value;
    private final String reason;

    ErrorList(String value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public String getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}
