package prompt.overshadowing.services.interfaces;

import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.model.Prompt;

import java.util.List;

public interface IPIIRevisionService {
    String LLMPromptRevision(String p, List<String> keywords) throws LLMRequestException;
    List<Prompt> piiReview(List<Prompt> prompts);
}
