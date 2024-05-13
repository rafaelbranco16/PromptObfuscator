package prompt.overshadowing.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import prompt.overshadowing.constants.Constants;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.model.Pii;
import prompt.overshadowing.model.Prompt;
import prompt.overshadowing.services.interfaces.ILlmModelService;
import prompt.overshadowing.services.interfaces.IPIIRevisionService;
import prompt.overshadowing.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PIIRevisionService implements IPIIRevisionService {
    @Inject
    ILlmModelService modelService;
    /**
     * Asks the LLM the prompt
     *
     * @return the content from LLM Response
     */
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 5000)
    public String LLMPromptRevision(String p, List<String> keywords) throws LLMRequestException {
        try {
            String prompt = Utils.generatePromptTemplateAsString(keywords, "keywords",
                    Constants.llmPromptRevisionTemplate);

            return this.modelService.generate(prompt, p);
        }catch (RuntimeException e) {
            throw new LLMRequestException(e.getMessage());
        }
    }
    /**
     * This function will review if all the PIIs were obfuscated
     * Sometime a given PII is seen as PII by the LLM and others not
     * To overcome that problem it was thought that it could be important
     * to retrieve all the PIIs from every prompt and review the whole document
     * @param prompts the prompts
     * @return the list with all the prompts reviewed
     */
    public List<Prompt> piiReview(List<Prompt> prompts) {
        List<Pii> piis = new ArrayList<>();
        for(Prompt prompt: prompts) {
            piis.addAll(prompt.getPiis());
        }

        for(Prompt prompt: prompts) {
            for(Pii p: piis) {
                try {
                    Integer.parseInt(p.getContent());
                }catch (NumberFormatException e) {
                    //prompt = prompt.getPrompt().replace(p.getId(), p.getContent());
                }
            }
        }

        return prompts;
    }
}
