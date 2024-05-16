package prompt.overshadowing.services;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import prompt.overshadowing.constants.Constants;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.model.Pii;
import prompt.overshadowing.model.Prompt;
import prompt.overshadowing.services.interfaces.ILlmModelService;
import prompt.overshadowing.services.interfaces.IPIIRevisionService;
import prompt.overshadowing.utils.Utils;

import java.time.Duration;
import java.util.*;

@ApplicationScoped
public class PIIRevisionService implements IPIIRevisionService {
    @Inject
    ILlmModelService modelService;
    @Inject
    Config config;
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

    /**
     * Asks the first model if there is any PII that needs revision
     * @param prompt the prompt that needs revision
     * @return if the revision is needed or not
     * @throws LLMRequestException if the app couldn't reach the LLM for some reason
     */
    @Override
    public boolean needsHigherRevision(Prompt prompt, List<String> keywords) throws LLMRequestException {
        if(prompt == null) return false;
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("keywords", keywords);

            String response = this.modelService.generate(
                    Utils.generatePromptFromTemplate(Constants.llmPromptRevisionAsking, variables).text(),
                    prompt.getPrompt()
            );
            return Boolean.parseBoolean(response);
        }catch (RuntimeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public void changeToRevisionModel() throws LLMRequestException {
        String baseUrl = config.getValue("revision.model.base.url", String.class);
        if(Objects.equals(baseUrl, "null")) baseUrl = null;
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(config.getValue("revision.model.api.key", String.class))
                .modelName(config.getValue("revision.model.name", String.class))
                .maxTokens(config.getValue("revision.model.max.tokens", Integer.class))
                .timeout(config.getValue("revision.model.timeout", Duration.class))
                .temperature(config.getValue("revision.model.temperature", Double.class))
                .logRequests(config.getValue("revision.model.log.requests", Boolean.class))
                .logResponses(config.getValue("revision.model.log.responses", Boolean.class))
                .build();

        this.modelService.changeModel(model);
    }
}
