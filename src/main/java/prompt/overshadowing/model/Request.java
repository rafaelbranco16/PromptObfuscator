package prompt.overshadowing.model;

import lombok.Getter;

@Getter
public class Request {
    /**
     * The id of the request
     */
    private RequestId id;
    /**
     * The prompt
     */
    private Prompt prompt;
    /**
     * The temperature to be used on the LLM
     */
    private String temperature;
    /**
     * Return a new request
     * @param id the id
     * @param prompt the prompt
     */
    public Request(RequestId id, Prompt prompt) {
        this.id  = id;
        this.prompt = prompt;
    }
    /**
     * Creates a new request
     * @param prompt the prompt
     * @return a new Request
     */
    public static Request create(RequestId id, Prompt prompt) {
        return new Request(id, prompt);
    }
}
