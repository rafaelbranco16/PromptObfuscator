package langfuse.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import prompt.overshadowing.utils.Utils;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * This class represents the trace ingestion type
 * <a href=https://api.reference.langfuse.com/#tag--Ingestion>Link for API Documentation</a>
 */
@Getter
public class Trace implements IngestionBody {
    @JsonProperty("id")
    private String traceId;
    private final String timestamp;
    private final String name;
    private final String userId;
    private final String input;
    private final String output;
    private final String sessionId;
    private final String release;
    private final String version;
    private final Object metadata;
    private final List<String> tags;
    @JsonProperty("public")
    private boolean p;

    public Trace(String input, String output, String sessionId, String release, String version
    , Object metadata, List<String> tags, boolean p) {
        this.traceId = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toString();
        this.name = Utils.getProperty("langfuse.trace.name.prefix") + this.traceId;
        this.userId = Utils.getProperty("langfuse.user.id");
        this.input = input;
        this.output = output;
        this.sessionId = sessionId;
        this.release = release;
        this.version = version;
        this.metadata = metadata;
        this.tags = tags;
        this.p = p;
    }
    public Trace(String input, String output, String sessionId
            ,Object metadata, List<String> tags, boolean p) {
        this.traceId = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toString();
        this.name = Utils.getProperty("langfuse.trace.name.prefix") + this.traceId;
        this.userId = Utils.getProperty("langfuse.user.id");
        this.input = input;
        this.output = output;
        this.sessionId = sessionId;
        this.release = "";
        this.version = "v0";
        this.metadata = metadata;
        this.tags = tags;
        this.p = p;
    }
}
