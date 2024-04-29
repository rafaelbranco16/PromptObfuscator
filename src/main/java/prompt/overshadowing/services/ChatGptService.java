package prompt.overshadowing.services;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.model.langfuse.*;
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

    @ConfigProperty(name = "langfuse.public.key")
    String username;
    @ConfigProperty(name = "langfuse.secret.key")
    String password;
    @ConfigProperty(name = "prompt.generator.trace.name")
    String generationTextName;

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
        AiMessage response = this.buildModel().generate(List.of(sysMessageObj, userMessageObj)).content();
        Batch b = createTrace(sysMessageObj, userMessageObj, response);
        ChatPrompt chatPrompt = this.createChatPrompt(sysMessageObj, userMessageObj, response);

        this.loggingService.sendPrompt(this.buildHeaderAuthorization(), chatPrompt);
        this.loggingService.sendBatch(buildHeaderAuthorization(), b);
        return response.text();
    }

    private Batch createTrace(SystemMessage sysMessage, UserMessage userMessage, AiMessage aiMessage) {
        IngestionBody body = new Trace(
                generationTextName,
                Utils.getProperty("langfuse.user.id"),
                sysMessage.text() + "\n" + userMessage.text(),
                aiMessage.text(),
                "sesId",
                "release",
                "v1",
                "",
                List.of("string"),
                false
        );
        Ingestion ingestion = new Ingestion(
                "trace-create",
                "",
                body

        );
        Batch b = new Batch();
        b.addIngestion(ingestion);
        return b;
    }

    private String buildHeaderAuthorization() {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    private ChatPrompt createChatPrompt(SystemMessage sysMessage, UserMessage userMessage, AiMessage aiMessage) {
        ChatPrompt prompt = new ChatPrompt("");
        prompt.addPrompt("System", sysMessage.text());
        prompt.addPrompt("User", userMessage.text());
        prompt.addPrompt("Ai", aiMessage.text());
        return prompt;
    }
}
