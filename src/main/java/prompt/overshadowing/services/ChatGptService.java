package prompt.overshadowing.services;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import langfuse.sdk.model.*;
import langfuse.sdk.service.LangFuseService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.services.interfaces.ILLMLoggingService;
import prompt.overshadowing.services.interfaces.ILlmModelService;
import prompt.overshadowing.utils.Utils;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ChatGptService implements ILlmModelService {
    @Inject
    @RestClient
    ILLMLoggingService loggingService;

    @Inject
    LangFuseService fuseService;

    //LangFuse integration properties
    @ConfigProperty(name = "langfuse.public.key")
    String username;
    @ConfigProperty(name = "langfuse.secret.key")
    String password;

    //LLM Configurations from the properties
    @ConfigProperty(name = "model.api.key")
    String modelKey;
    @ConfigProperty(name = "model.base.url")
    String modelBaseUrl;
    @ConfigProperty(name = "model.max.tokens")
    int modelMaxTokens;
    @ConfigProperty(name = "model.timeout")
    int modelTimeout;
    @ConfigProperty(name = "model.temperature")
    double modelTemperature;
    @ConfigProperty(name = "model.log.requests")
    boolean logRequests;
    @ConfigProperty(name = "model.log.responses")
    boolean logResponses;
    @ConfigProperty(name = "model.name")
    String modelName;

    @Override
    public ChatLanguageModel buildModel() {
        if(Objects.equals(modelBaseUrl, "null")) modelBaseUrl = null;
        return OpenAiChatModel.builder()
                .baseUrl(modelBaseUrl)
                .apiKey(modelKey)
                .modelName(modelName)
                .maxTokens(modelMaxTokens)
                .timeout(Duration.ofMillis(modelTimeout))
                .temperature(modelTemperature)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
    }

    /**
     * Asks the Llm and generates the response
     * @param sysMessage the System Message
     * @param userMessage the User Message
     * @return the generated response from the Llm
     */
    public String generate(String sysMessage, String userMessage) throws LLMRequestException {
        SystemMessage sysMessageObj = new SystemMessage(sysMessage);
        UserMessage userMessageObj = new UserMessage(userMessage);
        Response<AiMessage> response = this.buildModel().generate(List.of(sysMessageObj, userMessageObj));

        /*
        Batch trace = fuseService.createTrace(List.of(sysMessageObj, userMessageObj), response, "v1", "v1",
                "test",null, List.of(), true, null);
        ChatPrompt chatPrompt = fuseService.createChatPrompt(List.of(sysMessageObj, userMessageObj, response.content()));
        Batch gen = fuseService.createGeneration(sysMessageObj, userMessageObj,response, chatPrompt.getName(),
                trace.batch.get(0).getId());
        this.loggingService.sendPrompt(this.buildHeaderAuthorization(), chatPrompt);
        this.loggingService.sendBatch(buildHeaderAuthorization(), trace);
        this.loggingService.sendBatch(buildHeaderAuthorization(), gen);
        */

        return response.content().text();
    }

    private String buildHeaderAuthorization() {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }
}
