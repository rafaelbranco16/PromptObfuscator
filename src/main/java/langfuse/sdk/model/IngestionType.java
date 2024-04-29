package langfuse.sdk.model;

public enum IngestionType {
    TRACE_EVENT("trace-create"),
    CREATE_GENERATION_EVENT("generation-create"),
    UPDATE_GENERATION_EVENT("generation-update");

    private final String type;
    IngestionType(String type) {
        this.type = type;
    }
    public String type() {return this.type;}
}
