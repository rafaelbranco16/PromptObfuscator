package prompt.overshadowing.model.langfuse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public class Trace implements IngestionBody {
    public String id;
    public Instant timestamp;
    public String name;
    public String userId;
    public String input;
    public String output;
    public String sessionId;
    public String release;
    public String version;
    public Object metadata;
    public List<String> tags;

    @JsonProperty("public") // Using JsonbProperty for field named 'public'
    private boolean isPublic;

    public Trace() {
    }

    public Trace(String name, String userId, String input, String output,
                     String sessionId, String release, String version, Object metadata, List<String> tags, boolean isPublic) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.name = name;
        this.userId = userId;
        this.input = input;
        this.output = output;
        this.sessionId = sessionId;
        this.release = release;
        this.version = version;
        this.metadata = metadata;
        this.tags = tags;
        this.isPublic = isPublic;
    }
}