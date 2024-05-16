package prompt.overshadowing.unit.services;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.services.ChatGptService;
import prompt.overshadowing.services.interfaces.ILlmModelService;

import java.time.Duration;

@QuarkusTest
public class ChatGptServiceTest {
    @Inject
    ChatGptService service;
    @Inject
    Config config;
    @Test
    public void testWithEmptyConstructor() {
        // Arrange + Act
        ILlmModelService service = new ChatGptService();
        // Arrange + Act + Assert
        Assertions.assertDoesNotThrow(() -> new ChatGptService());
        Assertions.assertNotNull(service);
    }
    @Test
    public void testWithModelConstructor() {
        ChatLanguageModel model = createModelTemplate();
        // Arrange + Act
        ILlmModelService service = new ChatGptService();
        // Arrange + Act + Assert
        Assertions.assertDoesNotThrow(() -> new ChatGptService(model));
        Assertions.assertNotNull(service);
    }
    @Test
    public void testConstructorWithInvalidModel() {
        // Arrange + Act
        ILlmModelService service = new ChatGptService();
        // Assert
        Assertions.assertThrows(LLMRequestException.class,
                () -> new ChatGptService(null));
        Assertions.assertNotNull(service);
    }
    @Test
    public void testBuildBaseModel() {
        // Arrange + Act
        ChatLanguageModel model = service.buildBaseModel();
        // Assert
        Assertions.assertDoesNotThrow(() -> service.buildBaseModel());
        Assertions.assertNotNull(model);
    }
    @Test
    public void testChangeBaseModelWithValidModel() {
        // Arrange
        ILlmModelService service = new ChatGptService();
        ChatLanguageModel newModel = createModelTemplate();
        // Act + Assert
        Assertions.assertDoesNotThrow(() -> service.changeModel(newModel));
    }
    @Test
    public void testChangeBaseModelWithInvalidModel() {
        // Arrange
        ILlmModelService service = new ChatGptService();
        // Act + Assert
        Assertions.assertDoesNotThrow(() -> service.changeModel(null));
    }

    private ChatLanguageModel createModelTemplate() {
        return OpenAiChatModel.builder()
                .baseUrl("someUrl")
                .apiKey("someKey")
                .modelName("someName")
                .maxTokens(10)
                .timeout(Duration.ofMillis(10))
                .temperature(0.0)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
