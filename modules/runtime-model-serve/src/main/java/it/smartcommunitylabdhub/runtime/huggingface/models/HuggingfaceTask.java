package it.smartcommunitylabdhub.runtime.huggingface.models;

public enum HuggingfaceTask {
    sequence_classification("sequence_classification"),
    token_classification("token_classification"),
    fill_mask("fill_mask"),
    text_generation("text_generation"),
    text2text_generation("text2text_generation"),
    text_embedding("text_embedding");

    private final String task;

    HuggingfaceTask(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }
}
