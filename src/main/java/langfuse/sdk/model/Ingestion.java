package langfuse.sdk.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * The ingestion. This class implements the ingestion in the
 * <a href=https://api.reference.langfuse.com/#tag--Ingestion>API Documentation</a>
 */
@Getter
public class Ingestion {
    private final String type;
    private final String id;
    private final String timestamp;
    private final Object metadata;
    private final IngestionBody body;
    public Ingestion(IngestionType type, Object metadata, IngestionBody body) {
        this.type = type.type();
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toString();
        this.metadata = metadata;
        this.body = body;
    }
}
