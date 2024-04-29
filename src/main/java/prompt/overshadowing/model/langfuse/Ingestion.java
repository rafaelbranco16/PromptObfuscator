package prompt.overshadowing.model.langfuse;

import java.time.Instant;
import java.util.UUID;

public class Ingestion {
    public String type;
    public String id;
    public Instant timestamp;
    public Object metadata;
    public IngestionBody body;

    public Ingestion() {
    }

    public Ingestion(String type, Object metadata, IngestionBody body) {
        this.type = type;
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.metadata = metadata;
        this.body = body;
    }
}
