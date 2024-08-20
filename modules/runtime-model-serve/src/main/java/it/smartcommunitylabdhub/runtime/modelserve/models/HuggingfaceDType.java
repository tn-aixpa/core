package it.smartcommunitylabdhub.runtime.modelserve.models;

public enum HuggingfaceDType {

        AUTO("auto"),
        FLOAT16("float16"),
        FLOAT32("float32"),
        BFLOAT16("bfloat16"),
        FLOAT("float"),
        HALF("half");
    
        private final String dtype;
    
        HuggingfaceDType(String dtype) {
            this.dtype = dtype;
        }
    
        public String getDType() {
            return dtype;
        }
}