package prompt.overshadowing.services;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.services.interfaces.ILlmModelService;
import prompt.overshadowing.utils.Utils;

import java.time.Duration;
import java.util.List;

@ApplicationScoped
public class ChatGptService implements ILlmModelService {
    @Override
    public ChatLanguageModel buildModel() {
        return OpenAiChatModel.builder()
                .baseUrl(buildUrlString())
                .apiKey("not-needed")
                .modelName("local-model")
                .maxTokens(-1)
                .timeout(Duration.ofMillis(100000))
                .temperature(0.0)
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
        return this.buildModel().generate(List.of(sysMessageObj, userMessageObj)).content().text();
    }
}
