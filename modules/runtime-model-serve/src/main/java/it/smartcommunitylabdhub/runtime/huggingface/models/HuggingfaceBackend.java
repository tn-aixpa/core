package it.smartcommunitylabdhub.runtime.huggingface.models;

public enum HuggingfaceBackend {
    AUTO("auto"),
    VLLM("vllm"),
    HUGGINGFACE("huggingface");

    private final String backend;

    HuggingfaceBackend(String backend) {
        this.backend = backend;
    }

    public String getBackend() {
        return backend;
    }
}
