package it.smartcommunitylabdhub.runtime.huggingface.models;

public enum HuggingfaceTask {
    sequence_classification("sequence-classification"),
    token_classification("token-classification"),
    fill_mask("fill-mask"),
    text_generation("text-generation"),
    text2text_generation("text2text-generation");

    private final String task;

    HuggingfaceTask(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }
}
