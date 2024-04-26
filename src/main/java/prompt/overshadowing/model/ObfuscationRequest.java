package prompt.overshadowing.model;

import lombok.Getter;

import java.util.List;

@Getter
public class ObfuscationRequest extends Request{
    private List<String> keywords;
    /**
     * Constructor of the request
     * @param id the request id
     * @param prompt the prompt of the request
     * @param keywords the list of PIIs keywords
     */
    public ObfuscationRequest(RequestId id, Prompt prompt, List<String> keywords) {
        super(id, prompt);
        this.keywords = keywords;
    }
}
