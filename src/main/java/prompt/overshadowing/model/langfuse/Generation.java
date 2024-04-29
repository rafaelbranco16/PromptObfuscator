package prompt.overshadowing.model.langfuse;

import lombok.Getter;
import prompt.overshadowing.utils.Utils;

import java.time.Instant;
import java.util.Map;

@Getter
public class Generation {
    private final String traceId;
    private final String name;
    private final Instant startTime;
    private final Object metadata;
    private final Object input;
    private final Object output;
    private final String level;
    private final String statusMessage;
    private final String parentObservationId;
    private final String version;
    private final String id;
    private final Instant endTime;
    private final Instant completionStartTime;
    private final String model;
    private final Map<String, Object> modelParameters;
    private final Usage usage;
    private final String promptName;
    private final int promptVersion;

    public Generation(String traceId, Object metadata, Object input,
                      Object output, String level, String statusMessage, String parentObservationId,
                      String version, String id, Instant endTime, Instant completionStartTime,
                      String model, Map<String, Object> modelParameters, Usage usage, String promptName,
                      int promptVersion) {
        this.traceId = traceId;
        this.name = Utils.getProperty("langfuse.generation.name");
        this.startTime = Instant.now();
        this.metadata = metadata;
        this.input = input;
        this.output = output;
        this.level = level;
        this.statusMessage = statusMessage;
        this.parentObservationId = parentObservationId;
        this.version = version;
        this.id = id;
        this.endTime = Instant.now();
        this.completionStartTime = Instant.now();
        this.model = model;
        this.modelParameters = modelParameters;
        this.usage = usage;
        this.promptName = promptName;
        this.promptVersion = promptVersion;
    }
}

@Getter
class Usage {
    private final int input;
    private final int output;
    private final int total;
    private final String unit;
    private final int inputCost;
    private final int outputCost;
    private final int totalCost;
    public Usage(int input, int output, int total, String unit, int inputCost, int outputCost, int totalCost) {
        this.input = input;
        this.output = output;
        this.total = total;
        this.unit = unit;
        this.inputCost = inputCost;
        this.outputCost = outputCost;
        this.totalCost = totalCost;
    }
}