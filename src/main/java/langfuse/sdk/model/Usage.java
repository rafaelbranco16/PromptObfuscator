package langfuse.sdk.model;

import lombok.Getter;

@Getter
public class Usage {
    private final int input;
    private final int output;
    private final int total;
    private final String unit;
    private final double inputCost;
    private final double outputCost;
    private final double totalCost;

    public Usage(int input, int output, int total, String unit, double inputCost, double outputCost, double totalCost) {
        this.input = input;
        this.output = output;
        this.total = total;
        this.unit = unit;
        this.inputCost = inputCost;
        this.outputCost = outputCost;
        this.totalCost = totalCost;
    }
}
