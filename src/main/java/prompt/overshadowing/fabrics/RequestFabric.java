package prompt.overshadowing.fabrics;

import prompt.overshadowing.exceptions.InvalidIdException;
import prompt.overshadowing.exceptions.InvalidPromptException;
import prompt.overshadowing.model.ObfuscationRequest;
import prompt.overshadowing.model.Prompt;
import prompt.overshadowing.model.Request;
import prompt.overshadowing.model.RequestId;

import java.util.List;

public class RequestFabric {
    public static Request create(String prompt) {
        Prompt p = Prompt.create(prompt);
        RequestId id = RequestId.create();
        return Request.create(id, p);
    }
    public static ObfuscationRequest create(String id, String prompt, List<String> keywords)
            throws InvalidIdException, InvalidPromptException {
        RequestId idObj = RequestId.create(id);
        Prompt promptObj = Prompt.create(prompt);
        return new ObfuscationRequest(idObj, promptObj, keywords);
    }
    public static ObfuscationRequest create(String prompt, List<String> keywords) {
        RequestId id = new RequestId();
        Prompt promptObj = Prompt.create(prompt);
        return new ObfuscationRequest(id, promptObj, keywords);
    }
}
