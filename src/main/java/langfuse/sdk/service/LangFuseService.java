package langfuse.sdk.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.Response;
import jakarta.enterprise.context.ApplicationScoped;
import langfuse.sdk.model.*;
import prompt.overshadowing.utils.Utils;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LangFuseService {
    public Batch createTrace(List<ChatMessage> messages, Response<AiMessage> response,
                             String release, String version, String sessionId,
                             Object ibMetadata, List<String> tags, boolean p, Object ingMetadata) {
        StringBuilder inputString = new StringBuilder();
        for(ChatMessage message : messages) {
            inputString.append(message.toString());
        }
        IngestionBody body = new Trace(
                inputString.toString(),
                response.content().text(),
                sessionId,
                release,
                version,
                ibMetadata,
                tags,
                p
        );
        Ingestion ingestion = new Ingestion(
            IngestionType.TRACE_EVENT,
                ingMetadata,
                body
        );
        Batch batch = new Batch();
        batch.addIngestion(ingestion);
        return batch;
    }
    public ChatPrompt createChatPrompt(List<ChatMessage> messages) {
        ChatPrompt prompt = new ChatPrompt("");
        for(ChatMessage message : messages) {
            if(message instanceof SystemMessage) prompt.addPrompt("System", message.text());
            else if(message instanceof UserMessage) prompt.addPrompt("User", message.text());
            else if(message instanceof AiMessage) prompt.addPrompt("Ai", message.text());
        }
        return prompt;
    }

    public Batch createGeneration(
            SystemMessage systemMessage, UserMessage userMessage, Response<AiMessage> response, String promptName,
            String traceId) {
        double inputCost = Double.parseDouble(Utils.getProperty("langfuse.input.cost"));
        double outputCost = Double.parseDouble(Utils.getProperty("langfuse.output.cost"));
        int tokenUsage = response.tokenUsage().totalTokenCount();
        double totalCost = (double) response.tokenUsage().inputTokenCount() /1000 * inputCost +
                        (double) response.tokenUsage().outputTokenCount() /1000 * outputCost;
        Usage usage = new Usage(
            response.tokenUsage().inputTokenCount(),
                response.tokenUsage().outputTokenCount(),
                tokenUsage,
                "TOKENS",
                inputCost,
                outputCost,
                totalCost
        );

        IngestionBody body = new Generation(
            traceId,
                null,
                systemMessage.text() + userMessage.text(),
                response.content().text(),
                "DEBUG",
                null,
                null,
                null,
                UUID.randomUUID().toString(),
                null,
                null,
                null,
                null,
                usage,
                promptName,
                1
        );
        Ingestion ingestion = new Ingestion(
                IngestionType.CREATE_GENERATION_EVENT,
                null,
                body
        );
        Batch batch = new Batch();
        batch.addIngestion(ingestion);
        return batch;
    }
}
