package prompt.overshadowing.services;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import langfuse.sdk.model.Batch;
import langfuse.sdk.model.ChatPrompt;
import langfuse.sdk.model.Generation;
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

@ApplicationScoped
public class ChatGptService implements ILlmModelService {
    @Inject
    @RestClient
    ILLMLoggingService loggingService;

    @Inject
    LangFuseService fuseService;

    @ConfigProperty(name = "langfuse.public.key")
    String username;
    @ConfigProperty(name = "langfuse.secret.key")
    String password;

    @Override
    public ChatLanguageModel buildModel() {
        return OpenAiChatModel.builder()
                .baseUrl(buildUrlString())
                .apiKey("not-needed")
                .modelName("local-model")
                .maxTokens(-1)
                .timeout(Duration.ofMillis(100000))
                .temperature(0.0)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
    /**
     * Builds the URL String of the type
     * Domain:port/url
     * @return the URL string
     */
    private String buildUrlString() {
        return Utils.getProperty("lm.domain") +
                Utils.getProperty("lm.url");
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
        Batch trace = fuseService.createTrace(List.of(sysMessageObj, userMessageObj), response, "v1", "v1",
                "test",null, List.of(), true, null);
        ChatPrompt chatPrompt = fuseService.createChatPrompt(List.of(sysMessageObj, userMessageObj, response.content()));
        Batch gen = fuseService.createGeneration(sysMessageObj, userMessageObj,response, chatPrompt.getName(),
                trace.batch.get(0).getId());
        this.loggingService.sendPrompt(this.buildHeaderAuthorization(), chatPrompt);
        this.loggingService.sendBatch(buildHeaderAuthorization(), trace);
        this.loggingService.sendBatch(buildHeaderAuthorization(), gen);

        return response.content().text();
    }

    private String buildHeaderAuthorization() {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }
}
