package it.smartcommunitylabdhub.runtime.python.model;

public enum PythonVersion {
    PYTHON3_9("3.9"),
    PYTHON3_10("3.10");

    private final String version;

    PythonVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
